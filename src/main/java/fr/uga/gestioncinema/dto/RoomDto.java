package fr.uga.gestioncinema.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object for Room entity.
 */
@Data
@Builder
public class RoomDto {
    private Long id;
    private String name;
    private int numberOfSeats;
    private Long cinemaId;
}
