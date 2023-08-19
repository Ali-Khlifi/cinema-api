package fr.uga.gestioncinema.entities.projections;

import fr.uga.gestioncinema.entities.Film;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;

@Projection(name = "filmProj", types = Film.class)
public interface FilmProj {
    public Long getId();
    public String getTitre();
    public double getDuree();
    public String getRealisateur();
    public String getDescription();
    public String getPhoto();
    public Date getDateSortie();

}
