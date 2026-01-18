package fr.uga.gestioncinema.service.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import fr.uga.gestioncinema.configurations.StripeConfig;
import fr.uga.gestioncinema.dto.PaymentIntentResponse;
import fr.uga.gestioncinema.dto.PaymentTransactionDto;
import fr.uga.gestioncinema.entities.PaymentTransaction;
import fr.uga.gestioncinema.entities.PaymentTransaction.PaymentStatus;
import fr.uga.gestioncinema.entities.Reservation;
import fr.uga.gestioncinema.mappers.PaymentTransactionMapper;
import fr.uga.gestioncinema.repositories.PaymentTransactionRepository;
import fr.uga.gestioncinema.repositories.ReservationRepository;
import fr.uga.gestioncinema.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Payment Service Implementation - Handles Stripe payment operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentTransactionRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentTransactionMapper paymentMapper;
    private final StripeConfig stripeConfig;

    /**
     * Create a Stripe Payment Intent for a reservation
     */
    @Override
    public PaymentIntentResponse createPaymentIntent(Long reservationId) {
        log.info("Creating payment intent for reservation ID: {}", reservationId);

        // 1. Get reservation
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));

        // 2. Validate reservation status
        if (!Reservation.ReservationStatus.PENDING.equals(reservation.getStatus())) {
            throw new IllegalStateException("Cannot create payment for reservation with status: " + reservation.getStatus());
        }

        // 3. Validate terms accepted
        if (Boolean.FALSE.equals(reservation.getTermsAccepted())) {
            throw new IllegalStateException("Terms and conditions must be accepted before payment");
        }

        // 4. Check if payment already exists
        if (paymentRepository.existsByReservationId(reservationId)) {
            log.warn("Payment transaction already exists for reservation: {}", reservationId);
            throw new IllegalStateException("Payment transaction already exists for this reservation");
        }

        try {
            // 5. Calculate amount in cents (Stripe requires amount in smallest currency unit)
            long amountInCents = reservation.getTotalAmount()
                .multiply(BigDecimal.valueOf(100))
                .longValue();

            // 6. Create metadata for Stripe
            Map<String, String> metadata = new HashMap<>();
            metadata.put("reservationId", reservationId.toString());
            metadata.put("referenceNumber", reservation.getReferenceNumber());
            metadata.put("userId", reservation.getUser().getId().toString());

            // 7. Create Stripe Payment Intent
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency(stripeConfig.getCurrency().toLowerCase())
                .putAllMetadata(metadata)
                .setAutomaticPaymentMethods(
                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                        .setEnabled(true)
                        .build()
                )
                .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);
            
            log.info("✓ Stripe PaymentIntent created: {}", paymentIntent.getId());

            // 8. Save payment transaction in database
            PaymentTransaction payment = PaymentTransaction.builder()
                .reservation(reservation)
                .stripePaymentIntentId(paymentIntent.getId())
                .amount(reservation.getTotalAmount())
                .currency(stripeConfig.getCurrency())
                .status(PaymentStatus.PENDING)
                .build();

            paymentRepository.save(payment);
            log.info("✓ Payment transaction saved to database");

            // 9. Return response for frontend
            return PaymentIntentResponse.builder()
                .clientSecret(paymentIntent.getClientSecret())
                .amount(reservation.getTotalAmount())
                .currency(stripeConfig.getCurrency())
                .paymentIntentId(paymentIntent.getId())
                .build();

        } catch (StripeException e) {
            log.error("❌ Stripe API error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create payment intent: " + e.getMessage(), e);
        }
    }

    /**
     * Confirm payment after successful Stripe webhook
     */
    @Override
    public PaymentTransactionDto confirmPayment(String stripePaymentIntentId) {
        log.info("Confirming payment for PaymentIntent: {}", stripePaymentIntentId);

        PaymentTransaction payment = paymentRepository.findByStripePaymentIntentId(stripePaymentIntentId)
            .orElseThrow(() -> new IllegalArgumentException("Payment transaction not found: " + stripePaymentIntentId));

        // Update payment status
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(LocalDateTime.now());

        // Get payment method from Stripe if needed
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(stripePaymentIntentId);
            if (paymentIntent.getPaymentMethod() != null) {
                payment.setStripePaymentMethod(paymentIntent.getPaymentMethod());
            }
        } catch (StripeException e) {
            log.warn("Could not retrieve payment method from Stripe: {}", e.getMessage());
        }

        PaymentTransaction saved = paymentRepository.save(payment);
        log.info("✓ Payment confirmed successfully");

        return paymentMapper.toDto(saved);
    }

    /**
     * Handle payment failure
     */
    @Override
    public PaymentTransactionDto failPayment(String stripePaymentIntentId) {
        log.warn("Payment failed for PaymentIntent: {}", stripePaymentIntentId);

        PaymentTransaction payment = paymentRepository.findByStripePaymentIntentId(stripePaymentIntentId)
            .orElseThrow(() -> new IllegalArgumentException("Payment transaction not found: " + stripePaymentIntentId));

        payment.setStatus(PaymentStatus.FAILED);
        PaymentTransaction saved = paymentRepository.save(payment);

        return paymentMapper.toDto(saved);
    }

    /**
     * Find payment transaction by Stripe PaymentIntent ID
     */
    @Override
    @Transactional(readOnly = true)
    public PaymentTransactionDto findByStripePaymentIntentId(String stripePaymentIntentId) {
        return paymentRepository.findByStripePaymentIntentId(stripePaymentIntentId)
            .map(paymentMapper::toDto)
            .orElse(null);
    }

    /**
     * Get all payment transactions for a reservation
     */
    @Override
    @Transactional(readOnly = true)
    public List<PaymentTransactionDto> findByReservationId(Long reservationId) {
        return paymentMapper.toDtoList(paymentRepository.findByReservationId(reservationId));
    }
}









