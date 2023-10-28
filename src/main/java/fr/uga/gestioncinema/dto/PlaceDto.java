package fr.uga.gestioncinema.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlaceDto {
    private Long id;
    private int numero;
    private Long salleId;
}
