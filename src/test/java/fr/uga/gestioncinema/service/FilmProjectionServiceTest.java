package fr.uga.gestioncinema.service;

import fr.uga.gestioncinema.dto.FilmProjectionDto;
import fr.uga.gestioncinema.entities.FilmProjection;
import fr.uga.gestioncinema.mappers.FilmProjectionMapper;
import fr.uga.gestioncinema.repositories.FilmProjectionRepository;
import fr.uga.gestioncinema.service.impl.FilmProjectionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link FilmProjectionServiceImpl} using Mockito only.
 * No Spring context, no database — dependencies are mocked.
 */
@ExtendWith(MockitoExtension.class)
class FilmProjectionServiceTest {

    private static final LocalDate GIVEN_DATE = LocalDate.of(2026, 8, 8);

    private static final LocalTime GIVEN_TIME = LocalTime.of(12, 30, 00);
    private static final Long FILM_PROJECTION_ID = 10L;

    @Mock
    private FilmProjectionRepository repository;

    @Mock
    private FilmProjectionMapper mapper;

    @InjectMocks
    private FilmProjectionServiceImpl filmProjectionService;

    private FilmProjection filmProjection;
    private FilmProjectionDto filmProjectionDto;

    @BeforeEach
    void setUp() {

        // entity
        filmProjection = new FilmProjection();
        filmProjection.setId(FILM_PROJECTION_ID);
        filmProjection.setProjectionDate(GIVEN_DATE);
        filmProjection.setStartTime(GIVEN_TIME);
        filmProjection.setEndTime(LocalTime.of(14, 0));

        // DTO
        filmProjectionDto = FilmProjectionDto.builder()
                .id(FILM_PROJECTION_ID)
                .projectionDate(GIVEN_DATE)
                .startTime(GIVEN_TIME)
                .endTime(LocalTime.of(14, 0))
                .build();
    }
    @Test
    @DisplayName("findByDate should return the list of film projections for the given date")
    void findByDate_shouldReturnListOfFilmProjection() {
        // Arrange
        List<FilmProjection> entities = List.of(filmProjection);
        List<FilmProjectionDto> expected = List.of(filmProjectionDto);

        when(repository.findByProjectionDate(GIVEN_DATE)).thenReturn(entities);
        when(mapper.toDto(filmProjection)).thenReturn(filmProjectionDto);

        // Act
        List<FilmProjectionDto> result = filmProjectionService.findByDate(GIVEN_DATE);

        // Assert
        assertThat(result).isEqualTo(expected);
        verify(repository).findByProjectionDate(GIVEN_DATE);
        verify(mapper).toDto(filmProjection);
    }
    @Test
    @DisplayName("findUpcoming should return only upcoming film projections")
    void findUpcoming_shouldReturnListOfFilmProjection() {
        // Arrange — past (10:00) must be excluded, upcoming (16:00) must be kept
        FilmProjection pastProjection = new FilmProjection();
        pastProjection.setId(11L);
        pastProjection.setProjectionDate(GIVEN_DATE);
        pastProjection.setStartTime(LocalTime.of(10, 0));
        pastProjection.setEndTime(LocalTime.of(12, 0));

        FilmProjection upcomingProjection = new FilmProjection();
        upcomingProjection.setId(12L);
        upcomingProjection.setProjectionDate(GIVEN_DATE);
        upcomingProjection.setStartTime(LocalTime.of(16, 0));
        upcomingProjection.setEndTime(LocalTime.of(18, 0));

        FilmProjectionDto upcomingDto = FilmProjectionDto.builder()
                .id(12L)
                .projectionDate(GIVEN_DATE)
                .startTime(LocalTime.of(16, 0))
                .endTime(LocalTime.of(18, 0))
                .build();

        when(repository.findAll()).thenReturn(List.of(pastProjection, upcomingProjection));
        when(mapper.toDto(upcomingProjection)).thenReturn(upcomingDto);

        // Act
        List<FilmProjectionDto> result = filmProjectionService.findUpcoming(GIVEN_DATE, GIVEN_TIME);

        // Assert — only the upcoming one
        assertThat(result).isEqualTo(List.of(upcomingDto));
        verify(repository).findAll();
        verify(mapper).toDto(upcomingProjection);
    }
    @Test
    @DisplayName("isOngoing should return true when projection is currently running")
    void isOngoing_shouldReturnTrueWhenFilmProjectionIsOngoing() {
        // Arrange — start 12:00 / now 12:30 / end 14:00 → ongoing
        FilmProjection ongoingFilmProjection = new FilmProjection();
        ongoingFilmProjection.setId(FILM_PROJECTION_ID);
        ongoingFilmProjection.setProjectionDate(GIVEN_DATE);
        ongoingFilmProjection.setStartTime(LocalTime.of(12, 0));
        ongoingFilmProjection.setEndTime(LocalTime.of(14, 0));

        when(repository.findById(FILM_PROJECTION_ID)).thenReturn(Optional.of(ongoingFilmProjection));

        // Act
        boolean result = filmProjectionService.isOngoing(FILM_PROJECTION_ID, GIVEN_DATE, GIVEN_TIME);

        // Assert
        assertThat(result).isTrue();
        verify(repository).findById(FILM_PROJECTION_ID);
    }
}
