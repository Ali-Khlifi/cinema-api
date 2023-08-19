package fr.uga.gestioncinema.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CinemaDto {
    private Long id;
    private String name;
    private double longitude, latitude, altitude;
    private int nombreSalles;

    private Long villeId;
}
