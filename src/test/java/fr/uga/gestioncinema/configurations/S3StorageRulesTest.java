package fr.uga.gestioncinema.configurations;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class S3StorageRulesTest {

    @Test
    void productionRejectsAMissingBucket() {
        S3StorageProperties properties = new S3StorageProperties();
        properties.setRegion("eu-west-3");

        assertThatThrownBy(() -> S3StorageRules.requireProductionSettings(properties))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("CINEMA_S3_BUCKET");
    }

    @Test
    void productionRejectsAMissingRegion() {
        S3StorageProperties properties = configured("cinema-posters-prod", "");

        assertThatThrownBy(() -> S3StorageRules.requireProductionSettings(properties))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("CINEMA_S3_REGION");
    }

    @Test
    void productionRejectsAPrefixThatEscapesThePosterFolder() {
        S3StorageProperties properties = configured("cinema-posters-prod", "eu-west-3");
        properties.setKeyPrefix("../private/");

        assertThatThrownBy(() -> S3StorageRules.requireProductionSettings(properties))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("CINEMA_S3_KEY_PREFIX");
    }

    @Test
    void productionAcceptsABucketAndNormalizesThePrefix() {
        S3StorageProperties properties = configured("cinema-posters-prod", "eu-west-3");
        properties.setKeyPrefix("/posters");

        S3StorageRules.requireProductionSettings(properties);

        assertThat(properties.normalizedKeyPrefix()).isEqualTo("posters/");
        assertThat(properties.presignTtl()).isEqualTo(java.time.Duration.ofMinutes(15));
    }

    @Test
    void productionRejectsAPresignTtlOutsideTheAllowedWindow() {
        S3StorageProperties properties = configured("cinema-posters-prod", "eu-west-3");
        properties.setPresignTtlSeconds(10);

        assertThatThrownBy(() -> S3StorageRules.requireProductionSettings(properties))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("CINEMA_S3_PRESIGN_SECONDS");
    }

    private static S3StorageProperties configured(String bucket, String region) {
        S3StorageProperties properties = new S3StorageProperties();
        properties.setBucket(bucket);
        properties.setRegion(region);
        return properties;
    }
}
