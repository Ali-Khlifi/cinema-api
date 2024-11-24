package fr.uga.gestioncinema.repositories;

import fr.uga.gestioncinema.entities.FilmProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@RepositoryRestResource
@CrossOrigin("*")
public interface FilmProjectionRepository extends JpaRepository<FilmProjection, Long> {

    @Query(value = "SELECT DISTINCT ON (fp.film_id) * " +
            "FROM film_projection fp " +
            "WHERE fp.salle_id = :salleId", nativeQuery = true)
    List<FilmProjection> findUniqueProjectionsBySalleId(@Param("salleId") Long salleId);



}
