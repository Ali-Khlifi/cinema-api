package fr.uga.gestioncinema.entities.projections;

import fr.uga.gestioncinema.entities.Cinema;
import fr.uga.gestioncinema.entities.FilmProjection;
import fr.uga.gestioncinema.entities.Place;
import fr.uga.gestioncinema.entities.Salle;
import org.springframework.data.rest.core.config.Projection;

import java.util.Collection;


@Projection(name = "salleProj", types = Salle.class)
public interface SalleProj {
    Long getId();
    String getName();
    int getNombrePlaces();
    Cinema getCinema();
    Collection<Place> getPlaces();
    Collection<FilmProjection> getFilmProjections();
}
