package fr.uga.gestioncinema.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.uga.gestioncinema.entities.FilmProjection;
import fr.uga.gestioncinema.entities.Place;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TicketDto {
    private Long id;
    private String nomClient;
    private double prix;
    private int codePayement;
    private boolean reservee;
    private Long placeId;
    private Long filmProjectionId;
}
