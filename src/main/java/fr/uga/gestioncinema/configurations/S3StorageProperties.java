package fr.uga.gestioncinema.configurations;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

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

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
