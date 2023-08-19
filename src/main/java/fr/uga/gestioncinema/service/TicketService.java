package fr.uga.gestioncinema.service;

import fr.uga.gestioncinema.dto.TicketDto;
import fr.uga.gestioncinema.entities.Ticket;
import jakarta.validation.Valid;
import org.openapitools.model.TicketReservation;

import java.util.List;

public interface TicketService {
    List<TicketDto> payerTickets(TicketReservation ticketReservation);
    Ticket addTicket(TicketReservation ticketReservation);
    TicketDto fetchOne(Long id);
    TicketDto save(@Valid TicketDto dto);
    List<TicketDto> fetchAll();
    List<TicketDto> findByFilmProjection(Long filmProjectionId);
    TicketDto update(@Valid TicketDto dto);

    void delete(Long id);
}
