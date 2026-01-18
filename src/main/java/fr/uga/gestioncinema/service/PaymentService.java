package fr.uga.gestioncinema.service;

import fr.uga.gestioncinema.dto.PaymentIntentResponse;
import fr.uga.gestioncinema.dto.PaymentTransactionDto;

/**
 * Payment Service Interface - Handles Stripe payment operations
 */
public interface PaymentService {

    /**
     * Create a Stripe Payment Intent for a reservation
     * 
     * @param reservationId Reservation ID to create payment for
     * @return PaymentIntentResponse with client secret for frontend
     */
    PaymentIntentResponse createPaymentIntent(Long reservationId);

    /**
     * Confirm payment after successful Stripe webhook
     * 
     * @param stripePaymentIntentId Stripe PaymentIntent ID
     * @return Updated PaymentTransactionDto
     */
    PaymentTransactionDto confirmPayment(String stripePaymentIntentId);

    /**
     * Handle payment failure
     * 
     * @param stripePaymentIntentId Stripe PaymentIntent ID
     * @return Updated PaymentTransactionDto
     */
    PaymentTransactionDto failPayment(String stripePaymentIntentId);

    /**
     * Find payment transaction by Stripe PaymentIntent ID
     * 
     * @param stripePaymentIntentId Stripe PaymentIntent ID
     * @return PaymentTransactionDto if found
     */
    PaymentTransactionDto findByStripePaymentIntentId(String stripePaymentIntentId);

    /**
     * Get all payment transactions for a reservation
     * 
     * @param reservationId Reservation ID
     * @return List of PaymentTransactionDto
     */
    java.util.List<PaymentTransactionDto> findByReservationId(Long reservationId);
}









