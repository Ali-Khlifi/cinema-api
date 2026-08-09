package fr.uga.gestioncinema.repositories;

import fr.uga.gestioncinema.entities.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

/**
 * Repository for Cinema entity.
 */
@RepositoryRestResource
@CrossOrigin("*")
public interface CinemaRepository extends JpaRepository<Cinema, Long> {
    
    /**
     * Find cinemas by city name.
     * @param cityName the city name
     * @return list of cinemas in the specified city
     */
    List<Cinema> findByCityEntityName(String cityName);

    int countByCityEntityId(Long cityId);
}
