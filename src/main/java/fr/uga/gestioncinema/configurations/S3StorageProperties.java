package fr.uga.gestioncinema.configurations;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Poster bucket settings. Values come from the environment.
 * Access keys are not stored here: the AWS SDK default chain reads them.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "cinema.storage.s3")
public class S3StorageProperties {

    private String bucket = "";

    private String region = "";

    /** Object key prefix. Posters are stored only under this prefix. */
    private String keyPrefix = "posters/";

    /** Maximum accepted poster size. Default 5 MiB. */
    private long maxBytes = 5_242_880L;

    /** Lifetime of a presigned read URL. Default 15 minutes. */
    private long presignTtlSeconds = 900;

    public boolean isConfigured() {
        return !isBlank(bucket) && !isBlank(region);
    }

    /**
     * Prefix used to build object keys. Rejects path traversal.
     */
    public String normalizedKeyPrefix() {
        String prefix = isBlank(keyPrefix) ? "posters/" : keyPrefix.trim();
        while (prefix.startsWith("/")) {
            prefix = prefix.substring(1);
        }
        if (prefix.contains("..") || prefix.contains("\\")) {
            throw new IllegalStateException("CINEMA_S3_KEY_PREFIX must be a relative prefix without '..'");
        }
        if (!prefix.endsWith("/")) {
            prefix = prefix + "/";
        }
        return prefix;
    }

    /**
     * Lifetime of a browser-readable poster URL. A leaked URL must expire quickly.
     */
    public Duration presignTtl() {
        if (presignTtlSeconds < 60 || presignTtlSeconds > 3_600) {
            throw new IllegalStateException("CINEMA_S3_PRESIGN_SECONDS must be between 60 and 3600");
        }
        return Duration.ofSeconds(presignTtlSeconds);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
