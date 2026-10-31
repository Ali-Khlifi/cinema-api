package fr.uga.gestioncinema.configurations;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * Builds the S3 client only when a bucket and a region are configured.
 * Credentials stay on the AWS default chain (environment or IAM role).
 */
@Configuration
@EnableConfigurationProperties(S3StorageProperties.class)
public class S3StorageConfiguration {

    @Bean(destroyMethod = "close")
    @ConditionalOnExpression("!'${cinema.storage.s3.bucket:}'.trim().isEmpty() && !'${cinema.storage.s3.region:}'.trim().isEmpty()")
    public S3Client posterS3Client(S3StorageProperties properties) {
        properties.normalizedKeyPrefix();
        return S3Client.builder()
                .region(Region.of(properties.getRegion().trim()))
                .build();
    }
}
