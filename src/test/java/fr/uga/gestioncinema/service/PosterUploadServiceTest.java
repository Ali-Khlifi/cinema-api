package fr.uga.gestioncinema.service;

import fr.uga.gestioncinema.configurations.S3StorageProperties;
import fr.uga.gestioncinema.entities.Film;
import fr.uga.gestioncinema.exceptions.CustomErrorException;
import fr.uga.gestioncinema.exceptions.CustomErrorResponse;
import fr.uga.gestioncinema.exceptions.CustomErrorService;
import fr.uga.gestioncinema.repositories.FilmRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PosterUploadServiceTest {

    @Mock
    private ObjectProvider<S3Client> posterStorage;
    @Mock
    private S3Client storage;
    @Mock
    private FilmRepository filmRepository;
    @Mock
    private CustomErrorService customErrorService;

    private S3StorageProperties properties;
    private PosterUploadService service;

    @BeforeEach
    void setUp() {
        properties = new S3StorageProperties();
        properties.setBucket("cinema-posters-dev");
        properties.setRegion("eu-west-3");
        properties.setMaxBytes(32);
        service = new PosterUploadService(posterStorage, properties, filmRepository, customErrorService);
    }

    @Test
    void rejectsAFileThatIsNotAnImage() {
        stubStatus(HttpStatus.BAD_REQUEST);
        MockMultipartFile file = new MockMultipartFile("file", "note.txt", "text/plain", "hello world!!".getBytes());

        assertThatThrownBy(() -> service.upload(7L, file))
                .isInstanceOf(CustomErrorException.class);
        verify(storage, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void rejectsAPosterAboveTheSizeLimit() {
        stubStatus(HttpStatus.BAD_REQUEST);
        byte[] jpeg = new byte[40];
        jpeg[0] = (byte) 0xFF;
        jpeg[1] = (byte) 0xD8;
        jpeg[2] = (byte) 0xFF;
        MockMultipartFile file = new MockMultipartFile("file", "big.jpg", "image/jpeg", jpeg);

        assertThatThrownBy(() -> service.upload(7L, file))
                .isInstanceOf(CustomErrorException.class);
        verify(filmRepository, never()).findById(any());
    }

    @Test
    void reportsStorageUnavailableWhenNoBucketIsConfigured() {
        stubStatus(HttpStatus.SERVICE_UNAVAILABLE);
        when(posterStorage.getIfAvailable()).thenReturn(null);
        MockMultipartFile file = new MockMultipartFile("file", "poster.jpg", "image/jpeg", jpeg());

        assertThatThrownBy(() -> service.upload(7L, file))
                .isInstanceOf(CustomErrorException.class);
        verify(filmRepository, never()).findById(any());
    }

    @Test
    void storesThePosterAndDeletesThePreviousObject() {
        when(posterStorage.getIfAvailable()).thenReturn(storage);
        Film film = new Film();
        film.setId(7L);
        film.setPosterKey("posters/7/old.jpg");
        when(filmRepository.findById(7L)).thenReturn(Optional.of(film));
        when(filmRepository.save(film)).thenReturn(film);

        service.upload(7L, new MockMultipartFile("file", "poster.jpg", "image/jpeg", jpeg()));

        ArgumentCaptor<PutObjectRequest> put = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(storage).putObject(put.capture(), any(RequestBody.class));
        assertThat(put.getValue().bucket()).isEqualTo("cinema-posters-dev");
        assertThat(put.getValue().key()).startsWith("posters/7/").endsWith(".jpg");
        assertThat(put.getValue().contentType()).isEqualTo("image/jpeg");
        assertThat(film.getPosterKey()).isEqualTo(put.getValue().key());

        ArgumentCaptor<DeleteObjectRequest> deleted = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(storage).deleteObject(deleted.capture());
        assertThat(deleted.getValue().key()).isEqualTo("posters/7/old.jpg");
    }

    @Test
    void doesNotDeleteAPreviousKeyOutsideThePosterPrefix() {
        when(posterStorage.getIfAvailable()).thenReturn(storage);
        Film film = new Film();
        film.setId(7L);
        film.setPosterKey("other/secret.jpg");
        when(filmRepository.findById(7L)).thenReturn(Optional.of(film));
        when(filmRepository.save(film)).thenReturn(film);

        service.upload(7L, new MockMultipartFile("file", "poster.jpg", "image/jpeg", jpeg()));

        verify(storage, never()).deleteObject(any(DeleteObjectRequest.class));
    }

    private void stubStatus(HttpStatus status) {
        when(customErrorService.customErrorException(any(String.class), eq(status)))
                .thenReturn(new CustomErrorException("rejected", CustomErrorResponse.builder().status(status).build()));
    }

    private static byte[] jpeg() {
        byte[] content = new byte[16];
        content[0] = (byte) 0xFF;
        content[1] = (byte) 0xD8;
        content[2] = (byte) 0xFF;
        return content;
    }
}
