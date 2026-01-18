package fr.uga.gestioncinema.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object for Seat entity.
 */
@Data
@Builder
public class SeatDto {
    private Long id;
    private int number;
    private Long roomId;
}
