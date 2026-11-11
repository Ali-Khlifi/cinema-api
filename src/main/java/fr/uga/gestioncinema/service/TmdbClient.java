package fr.uga.gestioncinema.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.uga.gestioncinema.configurations.TmdbProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads at most {@link TmdbFilmParser#MAX_FILMS} popular films in French.
 * Detail calls are spaced out so a single import stays under the TMDB rate limit.
 */
@Service
@RequiredArgsConstructor
public class TmdbClient {

    static final String BASE = "https://api.themoviedb.org/3";
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);
    private static final long DETAIL_PAUSE_MILLIS = 300;

    private final TmdbProperties properties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public List<TmdbFilm> popularFilms() {
        if (!properties.isConfigured()) {
            throw new IllegalStateException("TMDB API key is missing");
        }
        List<Long> ids = new ArrayList<>();
        for (int page = 1; ids.size() < TmdbFilmParser.MAX_FILMS && page <= 3; page++) {
            List<Long> pageIds = TmdbFilmParser.popularIds(read("/movie/popular", "language=fr-FR&page=" + page));
            if (pageIds.isEmpty()) {
                break;
            }
            ids.addAll(pageIds);
        }
        List<TmdbFilm> films = new ArrayList<>();
        List<Long> selected = TmdbFilmParser.cap(ids);
        for (int index = 0; index < selected.size(); index++) {
            if (index > 0) {
                pause();
            }
            TmdbFilmParser.film(read(
                    "/movie/" + selected.get(index),
                    "language=fr-FR&append_to_response=credits,videos"))
                    .ifPresent(films::add);
        }
        return films;
    }

    private JsonNode read(String path, String query) {
        String key = URLEncoder.encode(properties.getApiKey().trim(), StandardCharsets.UTF_8);
        URI uri = URI.create(BASE + path + "?" + query + "&api_key=" + key);
        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(REQUEST_TIMEOUT)
                .header("Accept", "application/json")
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IllegalStateException("TMDB answered HTTP " + response.statusCode());
            }
            return objectMapper.readTree(response.body());
        } catch (InterruptedException failure) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("TMDB import was interrupted");
        } catch (IOException failure) {
            throw new IllegalStateException("TMDB could not be reached");
        }
    }

    private static void pause() {
        try {
            Thread.sleep(DETAIL_PAUSE_MILLIS);
        } catch (InterruptedException failure) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("TMDB import was interrupted");
        }
    }
}
