package fr.uga.gestioncinema.repositories;

import fr.uga.gestioncinema.entities.FilmProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for FilmProjection entity.
 */
@RepositoryRestResource
@CrossOrigin("*")
public interface FilmProjectionRepository extends JpaRepository<FilmProjection, Long> {

    /**
     * Find film projections by room ID.
     * Uses JPQL with JOIN FETCH to properly load film and room associations.
     * @param roomId the room ID
     * @return list of projections with loaded associations
     */
    @Query("SELECT fp FROM FilmProjection fp " +
           "JOIN FETCH fp.film f " +
           "JOIN FETCH fp.room r " +
           "WHERE r.id = :roomId " +
           "ORDER BY fp.projectionDate, fp.startTime")
    List<FilmProjection> findUniqueProjectionsByRoomId(@Param("roomId") Long roomId);

    /**
     * Find film projections by date
     * @param date
     * @return
     */
    List<FilmProjection> findByProjectionDate(LocalDate date);

}
