package fr.uga.gestioncinema.entities.projections;

import fr.uga.gestioncinema.entities.Seat;
import fr.uga.gestioncinema.entities.Ticket;
import org.springframework.data.rest.core.config.Projection;

/**
 * Spring Data REST projection for Ticket entity.
 * This is DEPRECATED and should be removed after full migration to DTOs.
 */
@Deprecated
@Projection(name = "ticketProj", types = Ticket.class)
public interface TicketProj {
    Long getId();
    Seat getSeat();
}
