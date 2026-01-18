package fr.uga.gestioncinema.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * Response DTO containing Stripe Payment Intent client secret
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentIntentResponse {

    /**
     * Stripe client secret for frontend
     */
    private String clientSecret;

    /**
     * Payment amount
     */
    private BigDecimal amount;

    /**
     * Currency code
     */
    private String currency;

    /**
     * Stripe PaymentIntent ID
     */
    private String paymentIntentId;
}









