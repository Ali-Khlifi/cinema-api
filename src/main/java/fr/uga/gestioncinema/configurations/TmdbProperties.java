package fr.uga.gestioncinema.configurations;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * TMDB read access. The API key comes from the environment and is never written here.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "cinema.tmdb")
public class TmdbProperties {

    private String apiKey = "";

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }
}
