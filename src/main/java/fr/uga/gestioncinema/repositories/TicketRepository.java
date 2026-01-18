package fr.uga.gestioncinema.repositories;

import fr.uga.gestioncinema.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Ticket entity.
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    /**
     * Find all tickets for a specific reservation.
     * @param reservationId the reservation ID
     * @return list of tickets
     */
    List<Ticket> findByReservationId(Long reservationId);
    
    /**
     * Find a ticket by its QR code.
     * @param qrCode the QR code
     * @return optional ticket
     */
    Optional<Ticket> findByQrCode(String qrCode);
    
    /**
     * Check if a ticket exists for a specific seat and reservation.
     * @param seatId the seat ID
     * @param reservationId the reservation ID
     * @return true if exists
     */
    boolean existsBySeatIdAndReservationId(Long seatId, Long reservationId);
    
    /**
     * Find all tickets for a specific film projection.
     * Joins through reservation table.
     * @param filmProjectionId the film projection ID
     * @return list of tickets
     */
    @Query("SELECT t FROM Ticket t " +
           "JOIN t.reservation r " +
           "WHERE r.filmProjection.id = :filmProjectionId")
    List<Ticket> findByFilmProjectionId(@Param("filmProjectionId") Long filmProjectionId);
}
