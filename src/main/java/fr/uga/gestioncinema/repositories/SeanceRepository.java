package fr.uga.gestioncinema.repositories;

import fr.uga.gestioncinema.entities.Seance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@RepositoryRestResource
@CrossOrigin("*")
public interface SeanceRepository extends JpaRepository<Seance, Long> {
    @Query("SELECT s FROM Seance s JOIN FilmProjection fp ON s.id = fp.seance.id WHERE fp.id = :filmProjectionId")
    List<Seance> findByFilmProjectionId(@Param("filmProjectionId") Long filmProjectionId);

}
