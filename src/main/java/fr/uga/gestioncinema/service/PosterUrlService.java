package fr.uga.gestioncinema.service;

import fr.uga.gestioncinema.configurations.S3StorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Named;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

/**
 * Builds a short-lived read URL for a private poster.
 * The object key itself is never returned to the client.
 * Signing is local; a missing bucket or a failed signature leaves the URL empty
 * so a film list still loads.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PosterUrlService {

    private final ObjectProvider<S3Presigner> posterPresigner;
    private final S3StorageProperties properties;

    @Named("presignPoster")
    public String presign(String posterKey) {
        if (posterKey == null || posterKey.isBlank()) {
            return null;
        }
        S3Presigner presigner = posterPresigner.getIfAvailable();
        if (presigner == null || !properties.isConfigured()) {
            return null;
        }
        String prefix = properties.normalizedKeyPrefix();
        if (!posterKey.startsWith(prefix) || posterKey.contains("..")) {
            log.warn("Refusing to sign a poster key outside the configured prefix");
            return null;
        }
        try {
            GetObjectRequest objectRequest = GetObjectRequest.builder()
                    .bucket(properties.getBucket().trim())
                    .key(posterKey)
                    .build();
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(properties.presignTtl())
                    .getObjectRequest(objectRequest)
                    .build();
            return presigner.presignGetObject(presignRequest).url().toExternalForm();
        } catch (RuntimeException failure) {
            log.warn("Poster URL could not be signed");
            return null;
        }
    }
}
