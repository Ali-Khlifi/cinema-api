/*package fr.uga.gestioncinema.service;

import fr.uga.gestioncinema.dao.FilmProjectionRepository;
import fr.uga.gestioncinema.dao.PlaceRepository;
import fr.uga.gestioncinema.dao.TicketRepository;
import fr.uga.gestioncinema.entities.FilmProjection;
import fr.uga.gestioncinema.entities.Place;
import fr.uga.gestioncinema.entities.Ticket;
import fr.uga.gestioncinema.web.formes.TicketForm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)*/
/*public class TicketServiceTest {

    @InjectMocks
    private TicketService ticketService;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private FilmProjectionRepository filmProjectionRepository;

    @Test*/
    //public void testAddTicket() {
      /*  // Initialiser les données pour le test
        TicketForm ticketForm = new TicketForm();
        ticketForm.setNomClient("John Doe");
        ticketForm.setCodePayement(1234);
        ticketForm.setPrix(15.0);
        ticketForm.setPlaceId(1L);
        ticketForm.setFilmProjectionId(2L);

        Place place = new Place();
        place.setId(1L);

        FilmProjection filmProjection = new FilmProjection();
        filmProjection.setId(2L);

        Ticket ticket = new Ticket();
        ticket.setNomClient("John Doe");
        ticket.setCodePayement(1234);
        ticket.setPrix(15.0);
        ticket.setReservee(true);
        ticket.setPlace(place);
        ticket.setFilmProjection(filmProjection);

        // Configurer les comportements mockés
        when(placeRepository.findById(1L)).thenReturn(Optional.of(place));
        when(filmProjectionRepository.findById(2L)).thenReturn(Optional.of(filmProjection));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        // Appeler la méthode à tester
        Ticket result = ticketService.addTicket(ticketForm);

        // Vérifier les résultats
        assertEquals("John Doe", result.getNomClient());
        assertEquals(1234, result.getCodePayement());
        assertEquals(15.0, result.getPrix());
        assertTrue(result.isReservee());
        assertEquals(place, result.getPlace());
        assertEquals(filmProjection, result.getFilmProjection());

        // Vérifier les interactions avec les mocks
        verify(placeRepository).findById(1L);
        verify(filmProjectionRepository).findById(2L);
        verify(ticketRepository).save(any(Ticket.class));
    }*/
//}