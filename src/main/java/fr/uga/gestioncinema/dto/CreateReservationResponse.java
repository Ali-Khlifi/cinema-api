package fr.uga.gestioncinema.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response DTO for reservation creation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationResponse {
    /** Reservation ID */
    private Long reservationId;
    
    /** Reference number */
    private String referenceNumber;
    
    /** Total amount */
    private BigDecimal totalAmount;
    
    /** Number of tickets */
    private Integer ticketCount;
    
    /** List of seat numbers reserved */
    private List<Integer> seatNumbers;
    
    /** Status */
    private String status;
    
    /** Message */
    private String message;
}


