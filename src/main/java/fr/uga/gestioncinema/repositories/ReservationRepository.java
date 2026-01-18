package fr.uga.gestioncinema.repositories;

import fr.uga.gestioncinema.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    Optional<Reservation> findByReferenceNumber(String referenceNumber);
    
    List<Reservation> findByUserId(Long userId);
    
    List<Reservation> findByStatus(String status);
    
    List<Reservation> findByExpirationDateBeforeAndStatus(LocalDateTime expirationDate, String status);
}











