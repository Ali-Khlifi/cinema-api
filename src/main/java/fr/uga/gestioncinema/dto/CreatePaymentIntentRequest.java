package fr.uga.gestioncinema.dto;

import lombok.*;

/**
 * Request DTO for creating a Stripe Payment Intent
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentIntentRequest {

    /**
     * Reservation ID to create payment for
     */
    private Long reservationId;
}









