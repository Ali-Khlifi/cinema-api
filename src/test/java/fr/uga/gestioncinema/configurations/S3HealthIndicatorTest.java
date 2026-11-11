package fr.uga.gestioncinema.configurations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.health.Status;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3HealthIndicatorTest {

    @Mock
    private ObjectProvider<S3Client> posterS3Client;
    @Mock
    private S3Client client;

    private S3StorageProperties properties;
    private S3HealthIndicator indicator;

    @BeforeEach
    void setUp() {
        properties = new S3StorageProperties();
        properties.setBucket("cinema-posters-dev");
        properties.setRegion("eu-north-1");
        indicator = new S3HealthIndicator(posterS3Client, properties);
    }

    @Test
    void staysUnknownWhenTheBucketIsNotConfigured() {
        properties.setBucket("");

        assertThat(indicator.health().getStatus()).isEqualTo(Status.UNKNOWN);
        verify(posterS3Client, never()).getIfAvailable();
    }

    @Test
    void isDownWhenTheClientIsMissing() {
        when(posterS3Client.getIfAvailable()).thenReturn(null);

        assertThat(indicator.health().getStatus()).isEqualTo(Status.DOWN);
    }

    @Test
    void isUpWhenTheBucketAnswers() {
        when(posterS3Client.getIfAvailable()).thenReturn(client);

        assertThat(indicator.health().getStatus()).isEqualTo(Status.UP);
        verify(client).headBucket(any(HeadBucketRequest.class));
    }

    @Test
    void isDownWhenTheBucketRefusesTheCall() {
        when(posterS3Client.getIfAvailable()).thenReturn(client);
        when(client.headBucket(any(HeadBucketRequest.class)))
                .thenThrow(S3Exception.builder().statusCode(403).message("AccessDenied").build());

        assertThat(indicator.health().getStatus()).isEqualTo(Status.DOWN);
    }
}
