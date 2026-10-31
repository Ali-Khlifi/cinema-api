package fr.uga.gestioncinema.configurations;

/**
 * Production gate for poster storage. Local startup stays allowed without a bucket.
 */
public final class S3StorageRules {

    private S3StorageRules() {
    }

    public static void requireProductionSettings(S3StorageProperties properties) {
        if (properties == null || isBlank(properties.getBucket())) {
            throw new IllegalStateException("CINEMA_S3_BUCKET is required in production");
        }
        if (isBlank(properties.getRegion())) {
            throw new IllegalStateException("CINEMA_S3_REGION is required in production");
        }
        properties.normalizedKeyPrefix();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
