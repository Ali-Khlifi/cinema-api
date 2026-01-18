package fr.uga.gestioncinema.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object for City entity.
 */
@Data
@Builder
public class CityDto {
    private Long id;
    private String name;
}
