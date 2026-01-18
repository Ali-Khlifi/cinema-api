package fr.uga.gestioncinema.repositories;

import fr.uga.gestioncinema.entities.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

/**
 * Repository for Seat entity.
 */
@RepositoryRestResource
@CrossOrigin("*")
public interface SeatRepository extends JpaRepository<Seat, Long> {
    
    /**
     * Find all seats for a specific room.
     * @param roomId the room ID
     * @return list of seats
     */
    List<Seat> findByRoomId(Long roomId);
}
