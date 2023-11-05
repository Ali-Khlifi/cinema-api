package fr.uga.gestioncinema.dto;

import fr.uga.gestioncinema.entities.Film;
import fr.uga.gestioncinema.entities.Salle;
import fr.uga.gestioncinema.entities.Seance;
import fr.uga.gestioncinema.entities.Ticket;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Data;

import java.util.Collection;
import java.util.Date;
@Data
@Builder
public class FilmProjectionDto {
    private Long id;
    private Date dateProjection;
    private Long salleId;
    private FilmDto film;
}
