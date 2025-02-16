package fr.uga.gestioncinema.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * SecurityConfig class configures security settings for the application,
 * enabling security filters and setting up OAuth2 login and logout behavior.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private JwtAuthConverter jwtAuthConverter;

    public SecurityConfig(JwtAuthConverter jwtAuthConverter) {
        this.jwtAuthConverter = jwtAuthConverter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(ar -> ar.requestMatchers("/*").permitAll())
                .authorizeHttpRequests(ar -> ar.anyRequest().authenticated())
                .oauth2ResourceServer(o2 -> o2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)))
                .headers(h -> h.frameOptions(fo -> fo.disable()))
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}















    /**
     * Configures the security filter chain for handling HTTP requests, OAuth2 login, and logout.
     *
     * @param http HttpSecurity object to define web-based security at the HTTP level
     * @return SecurityFilterChain for filtering and securing HTTP requests
     * @throws Exception in case of an error during configuration
     */
    /*@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Configures authorization rules for different endpoints
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/").permitAll() // Allows public access to the root URL
                        .anyRequest().authenticated() // Requires authentication for any other request
                )
                // Configures OAuth2 login settings
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/keycloak") // Sets custom login page for OAuth2 with Keycloak
                        .defaultSuccessUrl("/", true) // Redirects to "/" after successful login
                )
                // Configures logout settings
                .logout(logout -> logout
                        .logoutSuccessUrl("/").permitAll() // Redirects to the root URL on successful logout
                        .clearAuthentication(true) // Clears authentication details
                        .deleteCookies("JSESSIONID") // Deletes the session cookie
                );

        return http.build();
    }*/
