package fr.uga.gestioncinema.repositories;

import fr.uga.gestioncinema.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

/**
 * Repository for Room entity.
 */
@RepositoryRestResource
@CrossOrigin("*")
public interface RoomRepository extends JpaRepository<Room, Long> {

    /**
     * Find all rooms by cinema name.
     * @param cinemaName the cinema name
     * @return list of rooms
     */
    List<Room> findByCinemaName(String cinemaName);
    
    /**
     * Find all rooms by cinema ID.
     * @param cinemaId the cinema ID
     * @return list of rooms
     */
    List<Room> findByCinemaId(Long cinemaId);
}
