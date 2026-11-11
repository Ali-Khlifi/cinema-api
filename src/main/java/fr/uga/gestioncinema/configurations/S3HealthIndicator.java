package fr.uga.gestioncinema.configurations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

/**
 * Reports the poster bucket next to the database on /actuator/health.
 * A missing local bucket stays UNKNOWN so the API can still start.
 * HeadBucket needs s3:ListBucket on the bucket itself.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class S3HealthIndicator implements HealthIndicator {

    private final ObjectProvider<S3Client> posterS3Client;
    private final S3StorageProperties properties;

    @Override
    public Health health() {
        if (!properties.isConfigured()) {
            return Health.unknown().build();
        }
        S3Client client = posterS3Client.getIfAvailable();
        if (client == null) {
            return Health.down().build();
        }
        try {
            client.headBucket(HeadBucketRequest.builder()
                    .bucket(properties.getBucket().trim())
                    .build());
            return Health.up().build();
        } catch (S3Exception exception) {
            log.warn("S3 health check failed: HTTP {} {}", exception.statusCode(), errorCode(exception));
            return Health.down().build();
        } catch (RuntimeException exception) {
            log.warn("S3 health check failed: {}", exception.getClass().getSimpleName());
            return Health.down().build();
        }
    }

    private static String errorCode(S3Exception exception) {
        if (exception.awsErrorDetails() == null || exception.awsErrorDetails().errorCode() == null) {
            return "";
        }
        return exception.awsErrorDetails().errorCode();
    }
}
