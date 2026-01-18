package fr.uga.gestioncinema.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO representing a seat with its availability status for a specific film projection.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatAvailabilityDto {
    /** Seat ID */
    private Long seatId;
    
    /** Seat number */
    private int number;
    
    /** Is this seat reserved? */
    private boolean isReserved;
    
    /** Base price for this seat (if available) */
    private BigDecimal basePrice;
    
    /** Room ID */
    private Long roomId;
}

