package fr.uga.gestioncinema.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment Transaction Entity - Minimal traceability for Stripe payments
 * Delegates payment processing to Stripe API
 */
@Entity
@Table(name = "payment_transaction", indexes = {
    @Index(name = "idx_payment_reservation", columnList = "reservation_id"),
    @Index(name = "idx_payment_stripe_id", columnList = "stripe_payment_intent_id"),
    @Index(name = "idx_payment_status", columnList = "status"),
    @Index(name = "idx_payment_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Associated reservation
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id", nullable = false, foreignKey = @ForeignKey(name = "fk_payment_reservation"))
    private Reservation reservation;

    /**
     * Stripe PaymentIntent ID (unique identifier from Stripe)
     */
    @Column(name = "stripe_payment_intent_id", nullable = false, unique = true, length = 255)
    private String stripePaymentIntentId;

    /**
     * Payment amount (in EUR)
     */
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * Payment currency (default: EUR)
     */
    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "EUR";

    /**
     * Payment status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentStatus status;

    /**
     * Stripe payment method type (card, paypal, etc.)
     */
    @Column(name = "stripe_payment_method", length = 50)
    private String stripePaymentMethod;

    /**
     * Timestamp when payment was successfully completed
     */
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    /**
     * Creation timestamp (auto-managed by Hibernate)
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Update timestamp (auto-managed by Hibernate + trigger)
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Payment Status Enum
     */
    public enum PaymentStatus {
        PENDING,    // Payment intent created, awaiting payment
        SUCCESS,    // Payment succeeded
        FAILED,     // Payment failed
        REFUNDED,   // Payment refunded
        CANCELLED   // Payment cancelled by user or timeout
    }
}









