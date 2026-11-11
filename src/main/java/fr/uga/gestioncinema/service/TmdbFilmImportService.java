package fr.uga.gestioncinema.service;

import fr.uga.gestioncinema.configurations.TmdbProperties;
import fr.uga.gestioncinema.entities.Film;
import fr.uga.gestioncinema.exceptions.CustomErrorService;
import fr.uga.gestioncinema.repositories.CategorieRepository;
import fr.uga.gestioncinema.repositories.FilmRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Creates or updates films from TMDB. An existing poster and the legacy media column stay as they are.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TmdbFilmImportService {

    private final TmdbProperties properties;
    private final TmdbClient tmdbClient;
    private final FilmRepository filmRepository;
    private final CategorieRepository categorieRepository;
    private final CustomErrorService customErrorService;

    @Transactional
    public TmdbImportResult importPopularFilms() {
        if (!properties.isConfigured()) {
            throw customErrorService.customErrorException(
                    "TMDB import is not configured", HttpStatus.SERVICE_UNAVAILABLE);
        }
        List<TmdbFilm> incoming = readPopularFilms();
        int created = 0;
        int updated = 0;
        for (TmdbFilm film : incoming) {
            Optional<Film> existing = filmRepository.findByTmdbId(film.tmdbId());
            if (existing.isPresent()) {
                copyDetails(existing.get(), film, false);
                filmRepository.save(existing.get());
                updated++;
            } else {
                Film createdFilm = new Film();
                copyDetails(createdFilm, film, true);
                filmRepository.save(createdFilm);
                created++;
            }
        }
        return new TmdbImportResult(created, updated);
    }

    private List<TmdbFilm> readPopularFilms() {
        try {
            return tmdbClient.popularFilms();
        } catch (RuntimeException failure) {
            log.warn("TMDB import failed: {}", failure.getClass().getSimpleName());
            throw customErrorService.customErrorException("TMDB import failed", HttpStatus.BAD_GATEWAY);
        }
    }

    private void copyDetails(Film film, TmdbFilm incoming, boolean creating) {
        film.setTmdbId(incoming.tmdbId());
        film.setTitle(incoming.title());
        film.setDescription(incoming.description());
        film.setDuration(incoming.duration());
        film.setDirector(incoming.director());
        film.setReleaseDate(incoming.releaseDate());
        film.setTrailerYoutubeKey(incoming.trailerYoutubeKey());
        if (creating && incoming.categoryName() != null) {
            categorieRepository.findByName(incoming.categoryName()).ifPresent(film::setCategory);
        }
    }
}
