package fr.uga.gestioncinema.entities.projections;

import fr.uga.gestioncinema.entities.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.util.Collection;
import java.util.Date;

@Projection(name = "p1", types = {FilmProjection.class})
public interface ProjectionProj {
    public Long getId();
    public double getPrix();
    public Date getDateProjection();
    @Value("#{target.salle}")
    public SalleProj getSalle();
    @Value("#{target.film}")
    public FilmProj getFilm();
    public Seance getSeance();
    public Collection<Ticket> getTickets();




}
