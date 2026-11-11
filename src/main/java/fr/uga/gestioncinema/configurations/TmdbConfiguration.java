package fr.uga.gestioncinema.configurations;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TmdbProperties.class)
public class TmdbConfiguration {
}
