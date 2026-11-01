package fr.uga.gestioncinema.service;

import fr.uga.gestioncinema.configurations.S3StorageProperties;
import fr.uga.gestioncinema.entities.Film;
import fr.uga.gestioncinema.exceptions.CustomErrorService;
import fr.uga.gestioncinema.exceptions.ErrorCode;
import fr.uga.gestioncinema.repositories.FilmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

/**
 * Stores a film poster in the private bucket and records its object key.
 * The legacy bytea column is left unchanged.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PosterUploadService {

    private final ObjectProvider<S3Client> posterStorage;
    private final S3StorageProperties properties;
    private final FilmRepository filmRepository;
    private final CustomErrorService customErrorService;

    public void upload(Long filmId, MultipartFile file) {
        byte[] content = readAcceptedImage(file);
        PosterImage.Detected detected = PosterImage.detect(content)
                .orElseThrow(() -> customErrorService.customErrorException(
                        "Poster must be a jpeg, png, or webp image", HttpStatus.BAD_REQUEST));

        S3Client storage = posterStorage.getIfAvailable();
        if (storage == null || !properties.isConfigured()) {
            throw customErrorService.customErrorException(
                    "Poster storage is not configured", HttpStatus.SERVICE_UNAVAILABLE);
        }

        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> customErrorService.customErrorException(ErrorCode.FILM_NOT_FOUND, filmId));

        String key = properties.normalizedKeyPrefix() + filmId + "/" + UUID.randomUUID() + "." + detected.extension();
        String previousKey = film.getPosterKey();
        put(storage, key, detected.contentType(), content);
        try {
            film.setPosterKey(key);
            filmRepository.save(film);
        } catch (RuntimeException failure) {
            deleteQuietly(storage, key);
            throw failure;
        }
        deleteReplacedPoster(storage, previousKey, key);
    }

    private byte[] readAcceptedImage(MultipartFile file) {
        if (file == null || file.isEmpty() || file.getSize() > properties.getMaxBytes()) {
            throw customErrorService.customErrorException(
                    "Poster must be a non-empty image within the size limit", HttpStatus.BAD_REQUEST);
        }
        try {
            byte[] content = file.getBytes();
            if (content.length > properties.getMaxBytes()) {
                throw customErrorService.customErrorException(
                        "Poster must be a non-empty image within the size limit", HttpStatus.BAD_REQUEST);
            }
            return content;
        } catch (IOException failure) {
            throw customErrorService.customErrorException(
                    "Poster could not be read", HttpStatus.BAD_REQUEST);
        }
    }

    private void put(S3Client storage, String key, String contentType, byte[] content) {
        try {
            storage.putObject(PutObjectRequest.builder()
                    .bucket(properties.getBucket().trim())
                    .key(key)
                    .contentType(contentType)
                    .contentLength((long) content.length)
                    .build(), RequestBody.fromBytes(content));
        } catch (RuntimeException failure) {
            throw customErrorService.customErrorException(ErrorCode.EXTERNAL_SERVICE_ERROR);
        }
    }

    private void deleteReplacedPoster(S3Client storage, String previousKey, String newKey) {
        String prefix = properties.normalizedKeyPrefix();
        if (previousKey == null || previousKey.isBlank() || previousKey.equals(newKey)) {
            return;
        }
        if (!previousKey.startsWith(prefix) || previousKey.contains("..")) {
            log.warn("Refusing to delete a poster key outside the configured prefix");
            return;
        }
        deleteQuietly(storage, previousKey);
    }

    private void deleteQuietly(S3Client storage, String key) {
        try {
            storage.deleteObject(DeleteObjectRequest.builder()
                    .bucket(properties.getBucket().trim())
                    .key(key)
                    .build());
        } catch (RuntimeException failure) {
            log.warn("Poster object {} could not be deleted", key);
        }
    }
}
