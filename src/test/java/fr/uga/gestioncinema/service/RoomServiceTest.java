package fr.uga.gestioncinema.service;

import fr.uga.gestioncinema.entities.Room;
import fr.uga.gestioncinema.entities.Ticket;
import fr.uga.gestioncinema.mappers.RoomMapper;
import fr.uga.gestioncinema.repositories.RoomRepository;
import fr.uga.gestioncinema.repositories.TicketRepository;
import fr.uga.gestioncinema.service.impl.RoomServiceImpl;
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
 * Unit tests for {@link RoomServiceImpl} using Mockito only.
 * No Spring context, no database — dependencies are mocked.
 */
@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    private static final Long ROOM_ID = 10L;
    private static final Long FILM_PROJECTION_ID = 20L;
    private static final int ROOM_CAPACITY = 3;

    @Mock
    private RoomRepository repository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private RoomMapper mapper;

    @InjectMocks
    private RoomServiceImpl roomService;

    private Room room;

    @BeforeEach
    void setUp() {
        // Base fixture only — each test defines its own tickets
        room = new Room();
        room.setId(ROOM_ID);
        room.setNumberOfSeats(ROOM_CAPACITY);
    }

    @Test
    @DisplayName("isFull should return true when tickets count reaches room capacity")
    void isFull_shouldReturnTrueWhenRoomIsFullForProjection() {
        // Arrange — 3 tickets / capacity 3 → full
        List<Ticket> fullTickets = List.of(new Ticket(), new Ticket(), new Ticket());
        when(repository.findById(ROOM_ID)).thenReturn(Optional.of(room));
        when(ticketRepository.findByFilmProjectionId(FILM_PROJECTION_ID)).thenReturn(fullTickets);

        // Act
        boolean result = roomService.isFull(ROOM_ID, FILM_PROJECTION_ID);

        // Assert
        assertThat(result).isTrue();
        verify(repository).findById(ROOM_ID);
        verify(ticketRepository).findByFilmProjectionId(FILM_PROJECTION_ID);
    }
    @Test
    @DisplayName("isFull should return false when seats remain available")
    void isFull_shouldReturnFalseWhenSeatsRemain() {
        // Arrange — 2 tickets / capacity 3 → seats remain
        List<Ticket> occupiedTickets = List.of(new Ticket(), new Ticket());
        when(repository.findById(ROOM_ID)).thenReturn(Optional.of(room));
        when(ticketRepository.findByFilmProjectionId(FILM_PROJECTION_ID)).thenReturn(occupiedTickets);

        // Act
        boolean result = roomService.isFull(ROOM_ID, FILM_PROJECTION_ID);

        // Assert
        assertThat(result).isFalse();
        verify(repository).findById(ROOM_ID);
        verify(ticketRepository).findByFilmProjectionId(FILM_PROJECTION_ID);

    }

    @Test
    @DisplayName("countAvailableSeats should return the number of available seats")
    void countAvailableSeats_shouldReturnTheNumberOfAvailableSeats() {
        // Arrange — 2 tickets / capacity 3 → 1 seat available
        List<Ticket> occupiedTickets = List.of(new Ticket(), new Ticket());
        when(repository.findById(ROOM_ID)).thenReturn(Optional.of(room));
        when(ticketRepository.findByFilmProjectionId(FILM_PROJECTION_ID)).thenReturn(occupiedTickets);
        int expected = ROOM_CAPACITY - occupiedTickets.size();

        // Act
        int result = roomService.countAvailableSeats(ROOM_ID, FILM_PROJECTION_ID);

        // Assert
        assertThat(result).isEqualTo(expected);
        verify(repository).findById(ROOM_ID);
        verify(ticketRepository).findByFilmProjectionId(FILM_PROJECTION_ID);
    }
}
