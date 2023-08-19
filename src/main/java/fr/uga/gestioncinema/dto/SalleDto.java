package fr.uga.gestioncinema.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.uga.gestioncinema.entities.Cinema;
import fr.uga.gestioncinema.entities.FilmProjection;
import fr.uga.gestioncinema.entities.Place;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Data;

import java.util.Collection;

@Data
@Builder
public class SalleDto {
    private Long id;
    private String name;
    private int nombrePlaces;
    private Long cinemaId;
    private Collection<PlaceDto> places;
    private Collection<FilmProjectionDto> filmProjections;
}
