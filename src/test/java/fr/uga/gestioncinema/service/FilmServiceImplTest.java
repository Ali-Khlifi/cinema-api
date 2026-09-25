package fr.uga.gestioncinema.service;

import fr.uga.gestioncinema.dto.FilmDto;
import fr.uga.gestioncinema.entities.Category;
import fr.uga.gestioncinema.entities.Film;
import fr.uga.gestioncinema.mappers.FilmMapper;
import fr.uga.gestioncinema.repositories.FilmRepository;
import fr.uga.gestioncinema.service.impl.FilmServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link FilmServiceImpl} using Mockito only.
 * No Spring context, no database — dependencies are mocked.
 */
@ExtendWith(MockitoExtension.class)
class FilmServiceImplTest {

    private static final String FILM_TITLE = "Inception";
    private static final Long FILM_ID = 1L;
    private static final Long CATEGORY_ID = 1L;
    private static final byte[] MEDIA_FILM = "bnapapapapaapa".getBytes();

    private Film entity;
    private Category category;

    @Mock
    private FilmRepository filmRepository;

    @Mock
    private FilmMapper filmMapper;

    @InjectMocks
    private FilmServiceImpl filmService;

    @BeforeEach
    void setUp() {
        entity = new Film();
        entity.setId(FILM_ID);
        entity.setTitle(FILM_TITLE);

        category = new Category();
        category.setId(CATEGORY_ID);
    }

    @Test
    @DisplayName("findByTitle should return the film for the given title")
    void findByTitle_shouldReturnFilmWhenExists() {
        // Arrange
        FilmDto expected = FilmDto.builder()
                .id(FILM_ID)
                .title(FILM_TITLE)
                .build();

        when(filmRepository.findByTitle(FILM_TITLE)).thenReturn(Optional.of(entity));
        when(filmMapper.toDto(entity)).thenReturn(expected);

        // Act
        FilmDto result = filmService.findByTitle(FILM_TITLE);

        // Assert
        assertThat(result).isEqualTo(expected);
        verify(filmRepository).findByTitle(FILM_TITLE);
        verify(filmMapper).toDto(entity);
    }

    @Test
    @DisplayName("hasMedia should return true when film has media")
    void hasMedia_shouldReturnTrueWhenFilmHasMedia() {
        // Arrange
        entity.setMedia(MEDIA_FILM);
        when(filmRepository.findById(FILM_ID)).thenReturn(Optional.of(entity));

        // Act
        boolean result = filmService.hasMedia(FILM_ID);

        // Assert
        assertThat(result).isTrue();
        verify(filmRepository).findById(FILM_ID);
        verifyNoInteractions(filmMapper);
    }

    @Test
    @DisplayName("hasMedia should return false when film has no media")
    void hasMedia_shouldReturnFalseWhenFilmHasNoMedia() {
        // Arrange
        entity.setMedia(null);
        when(filmRepository.findById(FILM_ID)).thenReturn(Optional.of(entity));

        // Act
        boolean result = filmService.hasMedia(FILM_ID);

        // Assert
        assertThat(result).isFalse();
        verify(filmRepository).findById(FILM_ID);
    }

    @Test
    @DisplayName("hasMedia should return false when film has empty media")
    void hasMedia_shouldReturnFalseWhenFilmHasEmptyMedia() {
        // Arrange
        entity.setMedia(new byte[0]);
        when(filmRepository.findById(FILM_ID)).thenReturn(Optional.of(entity));

        // Act
        boolean result = filmService.hasMedia(FILM_ID);

        // Assert
        assertThat(result).isFalse();
        verify(filmRepository).findById(FILM_ID);
    }

    @Test
    @DisplayName("findByCategoryId should return all films with the same category id")
    void findByCategoryId_shouldReturnFilmsWhenCategoryHasFilms() {
        // Arrange — give distinct ids so Film (@Data) equals does not confuse Mockito verify
        Film film1 = new Film();
        film1.setId(1L);
        film1.setCategory(category);

        Film film2 = new Film();
        film2.setId(2L);
        film2.setCategory(category);

        FilmDto filmDto1 = FilmDto.builder()
                .id(1L)
                .categoryId(CATEGORY_ID)
                .build();
        FilmDto filmDto2 = FilmDto.builder()
                .id(2L)
                .categoryId(CATEGORY_ID)
                .build();

        List<FilmDto> expected = List.of(filmDto1, filmDto2);

        when(filmRepository.findByCategoryId(CATEGORY_ID)).thenReturn(List.of(film1, film2));
        when(filmMapper.toDto(film1)).thenReturn(filmDto1);
        when(filmMapper.toDto(film2)).thenReturn(filmDto2);

        // Act
        List<FilmDto> result = filmService.findByCategoryId(CATEGORY_ID);

        // Assert
        assertThat(result).isEqualTo(expected);
        verify(filmRepository).findByCategoryId(CATEGORY_ID);
        verify(filmMapper, times(2)).toDto(any(Film.class));
    }

    @Test
    @DisplayName("findByCategoryId should return empty list when no films match")
    void findByCategoryId_shouldReturnEmptyListWhenNoFilmsMatch() {
        // Arrange
        when(filmRepository.findByCategoryId(CATEGORY_ID)).thenReturn(List.of());

        // Act
        List<FilmDto> result = filmService.findByCategoryId(CATEGORY_ID);

        // Assert
        assertThat(result).isEmpty();
        verify(filmRepository).findByCategoryId(CATEGORY_ID);
        verifyNoInteractions(filmMapper);
    }
}
