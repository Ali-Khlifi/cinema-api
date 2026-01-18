package fr.uga.gestioncinema.entities.projections;

import fr.uga.gestioncinema.entities.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.time.LocalDate;
import java.util.Collection;

/**
 * Spring Data REST projection for FilmProjection entity.
 * This is DEPRECATED and should be removed after full migration to DTOs.
 */
@Deprecated
@Projection(name = "p1", types = {FilmProjection.class})
public interface ProjectionProj {
    Long getId();
    LocalDate getProjectionDate();
    
    @Value("#{target.room}")
    RoomProj getRoom();
    
    @Value("#{target.film}")
    FilmProj getFilm();
    
    Collection<Ticket> getTickets();
}
