package fr.uga.gestioncinema.web;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import fr.uga.gestioncinema.configurations.StripeConfig;
import fr.uga.gestioncinema.entities.Reservation;
import fr.uga.gestioncinema.repositories.ReservationRepository;
import fr.uga.gestioncinema.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Stripe Webhook Controller - Handles Stripe webhook events
 * 
 * ⚠️ IMPORTANT: This controller is NOT generated from OpenAPI specification.
 * 
 * Reason: Stripe webhooks require RAW payload access for signature verification.
 * OpenAPI binding automatically parses JSON, which breaks signature verification.
 * 
 * Stripe will send webhook events to this endpoint when payment status changes.
 * This is the AUTHORITATIVE source for payment confirmation.
 * 
 * Security: Always verify webhook signature to prevent fake webhook attacks!
 * 
 * @see <a href="https://stripe.com/docs/webhooks">Stripe Webhooks Documentation</a>
 */
@RestController
@RequestMapping("/payment/webhook")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment Webhook", description = "Stripe webhook endpoint (handles raw payloads)")
public class StripeWebhookController {

    private final PaymentService paymentService;
    private final ReservationRepository reservationRepository;
    private final StripeConfig stripeConfig;

    /**
     * Handle Stripe webhook events
     * 
     * Stripe sends webhooks for various events:
     * - payment_intent.succeeded: Payment completed successfully
     * - payment_intent.payment_failed: Payment failed
     * - payment_intent.canceled: Payment cancelled
     * 
     * @param payload Raw webhook payload from Stripe (as String for signature verification)
     * @param signatureHeader Stripe signature for verification
     * @return 200 OK to acknowledge receipt, or error status
     */
    @PostMapping
    @Operation(
        summary = "Stripe Webhook Handler",
        description = "Receives and processes Stripe webhook events. " +
                      "This endpoint is called by Stripe servers when payment status changes. " +
                      "Requires raw payload for signature verification."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Webhook received and processed successfully",
            content = @Content(schema = @Schema(type = "string", example = "Webhook received"))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid signature or malformed payload"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error processing webhook (Stripe will retry)"
        )
    })
    public ResponseEntity<String> handleWebhook(
        @Parameter(description = "Raw Stripe webhook event payload", required = true)
        @RequestBody String payload,
        
        @Parameter(description = "Stripe signature for verification (format: t=timestamp,v1=signature)", required = true)
        @RequestHeader("Stripe-Signature") String signatureHeader
    ) {
        log.info("📩 Received Stripe webhook");

        Event event;

        // 1. SECURITY: Verify webhook signature
        try {
            event = Webhook.constructEvent(
                payload,
                signatureHeader,
                stripeConfig.getWebhookSecret()
            );
            log.info("✓ Webhook signature verified");
        } catch (SignatureVerificationException e) {
            log.error("❌ Invalid webhook signature! Possible security threat.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid signature");
        } catch (Exception e) {
            log.error("❌ Error parsing webhook: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Webhook parsing error");
        }

        // 2. Handle event based on type
        String eventType = event.getType();
        log.info("Processing webhook event: {}", eventType);

        try {
            switch (eventType) {
                case "payment_intent.succeeded":
                    handlePaymentSuccess(event);
                    break;

                case "payment_intent.payment_failed":
                    handlePaymentFailed(event);
                    break;

                case "payment_intent.canceled":
                    handlePaymentCanceled(event);
                    break;

                default:
                    log.info("ℹ️  Unhandled event type: {}", eventType);
            }

            // 3. Always return 200 to Stripe (even for unhandled events)
            return ResponseEntity.ok("Webhook received");

        } catch (Exception e) {
            log.error("❌ Error processing webhook: {}", e.getMessage(), e);
            // Return 500 so Stripe will retry
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error processing webhook");
        }
    }

    /**
     * Handle successful payment
     */
    private void handlePaymentSuccess(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
            .getObject()
            .orElseThrow(() -> new IllegalStateException("Unable to deserialize PaymentIntent"));

        String paymentIntentId = paymentIntent.getId();
        String reservationId = paymentIntent.getMetadata().get("reservationId");

        log.info("✅ Payment succeeded for PaymentIntent: {} (Reservation: {})", paymentIntentId, reservationId);

        // 1. Confirm payment in database
        paymentService.confirmPayment(paymentIntentId);

        // 2. Update reservation status to CONFIRMED
        if (reservationId != null) {
            Reservation reservation = reservationRepository.findById(Long.parseLong(reservationId))
                .orElse(null);
            
            if (reservation != null) {
                reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
                reservation.setConfirmationDate(java.time.LocalDateTime.now());
                reservationRepository.save(reservation);
                log.info("✓ Reservation {} confirmed", reservationId);

                // TODO: Generate QR codes for tickets
                // TODO: Send confirmation email with tickets
            }
        }
    }

    /**
     * Handle failed payment
     */
    private void handlePaymentFailed(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
            .getObject()
            .orElseThrow(() -> new IllegalStateException("Unable to deserialize PaymentIntent"));

        String paymentIntentId = paymentIntent.getId();
        String reservationId = paymentIntent.getMetadata().get("reservationId");

        log.warn("❌ Payment failed for PaymentIntent: {} (Reservation: {})", paymentIntentId, reservationId);

        // 1. Mark payment as failed
        paymentService.failPayment(paymentIntentId);

        // 2. Optionally cancel reservation after max retries
        // For now, we keep reservation PENDING so user can retry
        log.info("ℹ️  Reservation {} remains PENDING (user can retry payment)", reservationId);
    }

    /**
     * Handle cancelled payment
     */
    private void handlePaymentCanceled(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
            .getObject()
            .orElseThrow(() -> new IllegalStateException("Unable to deserialize PaymentIntent"));

        String paymentIntentId = paymentIntent.getId();
        String reservationId = paymentIntent.getMetadata().get("reservationId");

        log.info("🚫 Payment cancelled for PaymentIntent: {} (Reservation: {})", paymentIntentId, reservationId);

        // Mark payment as cancelled
        paymentService.failPayment(paymentIntentId); // Reusing failPayment for now

        // Optionally update reservation status
        if (reservationId != null) {
            Reservation reservation = reservationRepository.findById(Long.parseLong(reservationId))
                .orElse(null);
            
            if (reservation != null && reservation.getStatus() == Reservation.ReservationStatus.PENDING) {
                reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
                reservation.setCancellationDate(java.time.LocalDateTime.now());
                reservation.setCancellationReason("Payment cancelled by user");
                reservationRepository.save(reservation);
                log.info("✓ Reservation {} cancelled", reservationId);
            }
        }
    }
}

