package fr.uga.gestioncinema.repositories;

import fr.uga.gestioncinema.entities.PaymentTransaction;
import fr.uga.gestioncinema.entities.PaymentTransaction.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for PaymentTransaction entity
 */
@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    /**
     * Find payment transaction by Stripe PaymentIntent ID
     */
    Optional<PaymentTransaction> findByStripePaymentIntentId(String stripePaymentIntentId);

    /**
     * Find all payment transactions for a reservation
     */
    List<PaymentTransaction> findByReservationId(Long reservationId);

    /**
     * Find payment transactions by status
     */
    List<PaymentTransaction> findByStatus(PaymentStatus status);

    /**
     * Check if a payment transaction exists for a reservation
     */
    boolean existsByReservationId(Long reservationId);
}









