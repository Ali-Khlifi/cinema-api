package fr.uga.gestioncinema.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Collection;
@Data
@Builder
public class VilleDto {
    private Long id;
    private String name;
    private double longitude, latitude, altitude;
}
