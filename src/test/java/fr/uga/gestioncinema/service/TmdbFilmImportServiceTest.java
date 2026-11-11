package fr.uga.gestioncinema.service;

import fr.uga.gestioncinema.configurations.TmdbProperties;
import fr.uga.gestioncinema.entities.Category;
import fr.uga.gestioncinema.entities.Film;
import fr.uga.gestioncinema.exceptions.CustomErrorException;
import fr.uga.gestioncinema.exceptions.CustomErrorResponse;
import fr.uga.gestioncinema.exceptions.CustomErrorService;
import fr.uga.gestioncinema.repositories.CategorieRepository;
import fr.uga.gestioncinema.repositories.FilmRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TmdbFilmImportServiceTest {

    @Mock
    private TmdbClient tmdbClient;
    @Mock
    private FilmRepository filmRepository;
    @Mock
    private CategorieRepository categorieRepository;
    @Mock
    private CustomErrorService customErrorService;

    private TmdbProperties properties;
    private TmdbFilmImportService service;

    @BeforeEach
    void setUp() {
        properties = new TmdbProperties();
        properties.setApiKey("tmdb-test-key");
        service = new TmdbFilmImportService(
                properties, tmdbClient, filmRepository, categorieRepository, customErrorService);
    }

    @Test
    void refusesToImportWhenTheApiKeyIsMissing() {
        properties.setApiKey("  ");
        when(customErrorService.customErrorException(any(String.class), eq(HttpStatus.SERVICE_UNAVAILABLE)))
                .thenReturn(new CustomErrorException("missing", CustomErrorResponse.builder().status(HttpStatus.SERVICE_UNAVAILABLE).build()));

        assertThatThrownBy(() -> service.importPopularFilms()).isInstanceOf(CustomErrorException.class);
        verify(tmdbClient, never()).popularFilms();
    }

    @Test
    void createsAFilmAndKeepsThePosterEmpty() {
        Category drama = new Category();
        drama.setName("Drame");
        when(tmdbClient.popularFilms()).thenReturn(ListOfOne());
        when(filmRepository.findByTmdbId(438631)).thenReturn(Optional.empty());
        when(categorieRepository.findByName("Drame")).thenReturn(Optional.of(drama));
        when(filmRepository.save(any(Film.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TmdbImportResult result = service.importPopularFilms();

        assertThat(result.created()).isEqualTo(1);
        assertThat(result.updated()).isZero();
        verify(filmRepository).save(org.mockito.ArgumentMatchers.argThat(film ->
                film.getPosterKey() == null
                        && film.getTmdbId().equals(438631)
                        && film.getCategory() == drama
                        && "Dune".equals(film.getTitle())));
    }

    @Test
    void updatesAnExistingFilmWithoutReplacingItsPoster() {
        Film existing = new Film();
        existing.setTmdbId(438631);
        existing.setPosterKey("posters/1/a.jpg");
        existing.setMedia(new byte[]{1, 2});
        existing.setTitle("Ancien titre");
        when(tmdbClient.popularFilms()).thenReturn(ListOfOne());
        when(filmRepository.findByTmdbId(438631)).thenReturn(Optional.of(existing));
        when(filmRepository.save(existing)).thenReturn(existing);

        TmdbImportResult result = service.importPopularFilms();

        assertThat(result.created()).isZero();
        assertThat(result.updated()).isEqualTo(1);
        assertThat(existing.getTitle()).isEqualTo("Dune");
        assertThat(existing.getTrailerYoutubeKey()).isEqualTo("french");
        assertThat(existing.getPosterKey()).isEqualTo("posters/1/a.jpg");
        assertThat(existing.getMedia()).containsExactly(1, 2);
        verify(categorieRepository, never()).findByName(any());
    }

    private static java.util.List<TmdbFilm> ListOfOne() {
        return java.util.List.of(new TmdbFilm(
                438631,
                "Dune",
                "Paul Atreides",
                155d,
                "Denis Villeneuve",
                LocalDate.of(2021, 9, 15),
                "french",
                "Drame"));
    }
}
