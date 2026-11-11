package fr.uga.gestioncinema.configurations;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;

/**
 * Refuses to boot the production profile without a private poster bucket and AWS credentials.
 */
@Component
@Profile("prod")
@RequiredArgsConstructor
public class ProdS3StartupCheck {

    private final S3StorageProperties properties;

    @PostConstruct
    void verify() {
        S3StorageRules.requireProductionSettings(properties);
        try {
            DefaultCredentialsProvider.create().resolveCredentials();
        } catch (RuntimeException exception) {
            throw new IllegalStateException(
                    "Production requires AWS credentials via AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY, or an IAM role",
                    exception);
        }
    }
}
