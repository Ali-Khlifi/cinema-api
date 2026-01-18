package fr.uga.gestioncinema.web;

import fr.uga.gestioncinema.dto.PaymentIntentResponse;
import fr.uga.gestioncinema.dto.PaymentTransactionDto;
import fr.uga.gestioncinema.mappers.PaymentIntentResponseMapper;
import fr.uga.gestioncinema.mappers.PaymentTransactionMapper;
import fr.uga.gestioncinema.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.api.PaymentApi;
import org.openapitools.model.CreatePaymentIntentRequest;
import org.openapitools.model.PaymentTransactionOpenApiModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Payment Controller - REST endpoints for payment operations
 * Implements PaymentApi generated from OpenAPI specification
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class PaymentController implements PaymentApi {

    private final PaymentService paymentService;
    private final PaymentTransactionMapper paymentMapper;
    private final PaymentIntentResponseMapper paymentIntentMapper;

    /**
     * Create a Stripe Payment Intent for a reservation
     * Frontend will use the client_secret to complete payment with Stripe.js
     */
    @Override
    public ResponseEntity<org.openapitools.model.PaymentIntentResponse> createPaymentIntent(
        CreatePaymentIntentRequest request
    ) {
        log.info("Received request to create payment intent for reservation: {}", request.getReservationId());
        
        try {
            PaymentIntentResponse response = paymentService.createPaymentIntent(
                request.getReservationId().longValue()
            );
            
            // Use mapper to convert DTO to OpenAPI model
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentIntentMapper.toOpenApiModel(response));
                
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Validation error: {}", e.getMessage());
            throw e; // Will be handled by GlobalExceptionHandler
        } catch (Exception e) {
            log.error("Error creating payment intent: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create payment intent", e);
        }
    }

    /**
     * Get payment transaction by Stripe PaymentIntent ID
     */
    @Override
    public ResponseEntity<PaymentTransactionOpenApiModel> getByPaymentIntentId(String paymentIntentId) {
        log.info("Getting payment transaction by PaymentIntent ID: {}", paymentIntentId);
        
        PaymentTransactionDto payment = paymentService.findByStripePaymentIntentId(paymentIntentId);
        
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(paymentMapper.toOpenApiModel(payment));
    }

    /**
     * Get all payment transactions for a reservation
     */
    @Override
    public ResponseEntity<List<PaymentTransactionOpenApiModel>> getByReservationId(Long reservationId) {
        log.info("Getting payment transactions for reservation: {}", reservationId);
        
        List<PaymentTransactionDto> payments = paymentService.findByReservationId(reservationId);
        return ResponseEntity.ok(paymentMapper.toOpenApiModelList(payments));
    }
}

