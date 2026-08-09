package fr.uga.gestioncinema.service;

import fr.uga.gestioncinema.entities.Cinema;
import fr.uga.gestioncinema.entities.Room;
import fr.uga.gestioncinema.mappers.CinemaMapper;
import fr.uga.gestioncinema.mappers.CityMapper;
import fr.uga.gestioncinema.repositories.CinemaRepository;
import fr.uga.gestioncinema.repositories.RoomRepository;
import fr.uga.gestioncinema.service.impl.CinemaServiceImpl;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link CinemaServiceImpl} using Mockito only.
 * No Spring context, no database — dependencies are mocked.
 */
@ExtendWith(MockitoExtension.class)
class CinemaServiceTest {

    private static final Long CITY_ID = 10L;
    private static final Long CINEMA_ID = 1L;
    private static final int EXPECTED_CINEMA_COUNT = 3;
    // 50 + 80 + 70 = 200
    private static final int EXPECTED_SEATS_SUM = 200;

    @Mock
    private CinemaRepository repository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private CinemaMapper mapper;

    @Mock
    private CityMapper cityMapper;

    @InjectMocks
    private CinemaServiceImpl cinemaService;

    // Données partagées — recréées avant chaque test (isolation)
    private List<Room> rooms;
    private Cinema cinema;

    /**
     * Comme CategoryServiceTest : on prépare le "matériel" ici,
     * pour garder les tests focalisés sur Arrange (mock) / Act / Assert.
     */
    @BeforeEach
    void setUp() {
        Room room1 = new Room();
        room1.setNumberOfSeats(50);

        Room room2 = new Room();
        room2.setNumberOfSeats(80);

        Room room3 = new Room();
        room3.setNumberOfSeats(70);

        rooms = List.of(room1, room2, room3);

        // Cinema entity
        cinema = new Cinema();
        cinema.setId(CINEMA_ID);
        cinema.setHasCoffee(true);
    }

    @Test
    @DisplayName("countByCityId should return the number of cinemas for a given city")
    void countByCityId_shouldReturnCinemaCount() {
        // Arrange
        when(repository.countByCityEntityId(CITY_ID)).thenReturn(EXPECTED_CINEMA_COUNT);

        // Act
        int result = cinemaService.countByCityId(CITY_ID);

        // Assert
        assertThat(result).isEqualTo(EXPECTED_CINEMA_COUNT);
        verify(repository).countByCityEntityId(CITY_ID);
    }

    @Test
    @DisplayName("countSeatsByCinemaId should return the total number of seats for a cinema")
    void countSeatsByCinemaId_shouldReturnTotalSeats() {
        // Arrange — rooms déjà prêtes via setUp()
        when(roomRepository.findByCinemaId(CINEMA_ID)).thenReturn(rooms);

        // Act
        int result = cinemaService.countSeatsByCinemaId(CINEMA_ID);

        // Assert
        assertThat(result).isEqualTo(EXPECTED_SEATS_SUM);
        verify(roomRepository).findByCinemaId(CINEMA_ID);
    }
    @Test
    @DisplayName("hasCoffee should return true when cinema has a coffee shop")
    void hasCoffee_shouldReturnTrueWhenCinemaHasCoffee() {
        // Arrange — on mocke le repository, PAS le service
        when(repository.findById(CINEMA_ID)).thenReturn(Optional.of(cinema));

        // Act
        boolean result = cinemaService.hasCoffee(CINEMA_ID);

        // Assert
        assertThat(result).isTrue();
        verify(repository).findById(CINEMA_ID);
    }
}
