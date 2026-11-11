package fr.uga.gestioncinema.service;

import java.time.LocalDate;

/**
 * One film read from TMDB, already limited to the fields this application stores.
 */
public record TmdbFilm(
        int tmdbId,
        String title,
        String description,
        double duration,
        String director,
        LocalDate releaseDate,
        String trailerYoutubeKey,
        String categoryName
) {
}
