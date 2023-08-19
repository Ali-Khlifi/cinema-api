package fr.uga.gestioncinema.entities.projections;

import fr.uga.gestioncinema.entities.Place;
import fr.uga.gestioncinema.entities.Ticket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

@Projection(name ="ticketProj", types = Ticket.class)
public interface TicketProj {
    public Long getId();
    public String getNomClient();
    public double getPrix();
    public Integer getCodePayement();
    public boolean getReservee();
    public Place getPlace();
}
