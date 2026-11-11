package fr.uga.gestioncinema.service;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Reads the TMDB JSON this import needs. Network access stays in {@link TmdbClient}.
 */
public final class TmdbFilmParser {

    public static final int MAX_FILMS = 50;

    private static final int TITLE_MAX = 255;
    private static final int DIRECTOR_MAX = 255;
    private static final Set<String> KNOWN_CATEGORIES = Set.of(
            "Action", "Comédie", "Drame", "Horreur", "Animation", "Documentaire");

    private TmdbFilmParser() {
    }

    public static List<Long> cap(List<Long> ids) {
        if (ids.size() <= MAX_FILMS) {
            return List.copyOf(ids);
        }
        return List.copyOf(ids.subList(0, MAX_FILMS));
    }

    public static List<Long> popularIds(JsonNode page) {
        List<Long> ids = new ArrayList<>();
        JsonNode results = page.path("results");
        if (!results.isArray()) {
            return ids;
        }
        for (JsonNode film : results) {
            if (film.path("id").canConvertToLong()) {
                ids.add(film.path("id").asLong());
            }
        }
        return ids;
    }

    public static Optional<TmdbFilm> film(JsonNode movie) {
        if (!movie.path("id").canConvertToInt()) {
            return Optional.empty();
        }
        String title = clip(movie.path("title").asText(""), TITLE_MAX);
        if (title == null) {
            return Optional.empty();
        }
        return Optional.of(new TmdbFilm(
                movie.path("id").asInt(),
                title,
                clip(movie.path("overview").asText(""), Integer.MAX_VALUE),
                movie.path("runtime").isNumber() ? movie.path("runtime").asDouble() : 0d,
                clip(directorName(movie), DIRECTOR_MAX),
                releaseDate(movie.path("release_date").asText("")),
                trailerKey(movie.path("videos").path("results")),
                categoryName(movie.path("genres")),
                posterPath(movie)));
    }

    private static String directorName(JsonNode movie) {
        for (JsonNode person : movie.path("credits").path("crew")) {
            if ("Director".equals(person.path("job").asText()) && !person.path("name").asText("").isBlank()) {
                return person.path("name").asText();
            }
        }
        return null;
    }

    /**
     * A French trailer wins over an English one. A trailer wins over a teaser.
     * The stored value is the YouTube id, not a full URL.
     */
    private static String trailerKey(JsonNode videos) {
        String bestKey = null;
        int bestScore = -1;
        if (!videos.isArray()) {
            return null;
        }
        for (JsonNode video : videos) {
            if (!"YouTube".equals(video.path("site").asText())) {
                continue;
            }
            String type = video.path("type").asText("");
            if (!"Trailer".equals(type) && !"Teaser".equals(type)) {
                continue;
            }
            String key = video.path("key").asText("").trim();
            if (key.isEmpty()) {
                continue;
            }
            int score = ("Trailer".equals(type) ? 8 : 0)
                    + ("fr".equalsIgnoreCase(video.path("iso_639_1").asText("")) ? 4 : 0)
                    + (video.path("official").asBoolean(false) ? 1 : 0);
            if (score > bestScore) {
                bestScore = score;
                bestKey = key;
            }
        }
        return bestKey;
    }

    /**
     * TMDB poster paths look like /abc.jpg. Anything else is refused.
     */
    static String posterPath(JsonNode movie) {
        String path = movie.path("poster_path").asText("").trim();
        if (!path.matches("/[A-Za-z0-9._-]+")) {
            return null;
        }
        return path;
    }

    private static String categoryName(JsonNode genres) {
        if (!genres.isArray()) {
            return null;
        }
        for (JsonNode genre : genres) {
            String name = genre.path("name").asText("");
            if (KNOWN_CATEGORIES.contains(name)) {
                return name;
            }
        }
        return null;
    }

    private static LocalDate releaseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }

    private static String clip(String value, int max) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed.length() <= max ? trimmed : trimmed.substring(0, max);
    }
}
