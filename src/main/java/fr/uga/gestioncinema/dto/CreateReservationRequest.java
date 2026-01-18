package fr.uga.gestioncinema.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for creating a new reservation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationRequest {
    /** Film projection ID */
    private Long filmProjectionId;
    
    /** List of seat IDs to reserve */
    private List<Long> seatIds;
    
    /** Price type ID (1=NORMAL, 2=REDUCED, etc.) - default to NORMAL if not specified */
    private Long priceTypeId;
    
    /** Terms and conditions accepted */
    private Boolean termsAccepted;
    
    /** Marketing consent (optional) */
    private Boolean marketingConsent;
}


