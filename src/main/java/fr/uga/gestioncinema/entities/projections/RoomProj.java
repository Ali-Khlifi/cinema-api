package fr.uga.gestioncinema.entities.projections;

import fr.uga.gestioncinema.entities.Cinema;
import fr.uga.gestioncinema.entities.FilmProjection;
import fr.uga.gestioncinema.entities.Room;
import fr.uga.gestioncinema.entities.Seat;
import org.springframework.data.rest.core.config.Projection;

import java.util.Collection;

/**
 * Spring Data REST projection for Room entity.
 * This is DEPRECATED and should be removed after full migration to DTOs.
 */
@Deprecated
@Projection(name = "roomProj", types = Room.class)
public interface RoomProj {
    Long getId();
    String getName();
    int getNumberOfSeats();
    Cinema getCinema();
    Collection<Seat> getSeats();
    Collection<FilmProjection> getFilmProjections();
}
