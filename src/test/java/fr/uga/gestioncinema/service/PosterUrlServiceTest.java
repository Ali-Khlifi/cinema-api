package fr.uga.gestioncinema.service;

import fr.uga.gestioncinema.configurations.S3StorageProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URI;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PosterUrlServiceTest {

    @Mock
    private ObjectProvider<S3Presigner> posterPresigner;
    @Mock
    private S3Presigner presigner;

    private S3StorageProperties properties;
    private PosterUrlService service;

    @BeforeEach
    void setUp() {
        properties = new S3StorageProperties();
        properties.setBucket("cinema-posters-dev");
        properties.setRegion("eu-west-3");
        service = new PosterUrlService(posterPresigner, properties);
    }

    @Test
    void returnsNullWhenTheFilmHasNoPoster() {
        assertThat(service.presign(null)).isNull();
        assertThat(service.presign("  ")).isNull();
        verify(posterPresigner, never()).getIfAvailable();
    }

    @Test
    void returnsNullWhenStorageIsNotConfigured() {
        when(posterPresigner.getIfAvailable()).thenReturn(null);

        assertThat(service.presign("posters/7/a.jpg")).isNull();
        verify(presigner, never()).presignGetObject(any(GetObjectPresignRequest.class));
    }

    @Test
    void refusesToSignAKeyOutsideThePosterPrefix() {
        when(posterPresigner.getIfAvailable()).thenReturn(presigner);

        assertThat(service.presign("other/secret.jpg")).isNull();
        assertThat(service.presign("posters/../secret.jpg")).isNull();
        verify(presigner, never()).presignGetObject(any(GetObjectPresignRequest.class));
    }

    @Test
    void signsAPosterKeyForFifteenMinutes() throws Exception {
        when(posterPresigner.getIfAvailable()).thenReturn(presigner);
        URI signed = URI.create("https://cinema-posters-dev.s3.eu-west-3.amazonaws.com/posters/7/a.jpg?X-Amz-Signature=abc");
        PresignedGetObjectRequest presigned = mock(PresignedGetObjectRequest.class);
        when(presigned.url()).thenReturn(signed.toURL());
        when(presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenReturn(presigned);

        assertThat(service.presign("posters/7/a.jpg")).isEqualTo(signed.toString());

        ArgumentCaptor<GetObjectPresignRequest> captor = ArgumentCaptor.forClass(GetObjectPresignRequest.class);
        verify(presigner).presignGetObject(captor.capture());
        GetObjectPresignRequest request = captor.getValue();
        assertThat(request.signatureDuration()).isEqualTo(Duration.ofMinutes(15));
        assertThat(request.getObjectRequest().bucket()).isEqualTo("cinema-posters-dev");
        assertThat(request.getObjectRequest().key()).isEqualTo("posters/7/a.jpg");
    }

    @Test
    void returnsNullWhenSigningFails() {
        when(posterPresigner.getIfAvailable()).thenReturn(presigner);
        when(presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenThrow(new IllegalStateException("credentials unavailable"));

        assertThat(service.presign("posters/7/a.jpg")).isNull();
    }
}
