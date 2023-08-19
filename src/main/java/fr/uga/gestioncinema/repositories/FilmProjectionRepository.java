package fr.uga.gestioncinema.repositories;

import fr.uga.gestioncinema.entities.Cinema;
import fr.uga.gestioncinema.entities.FilmProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@RepositoryRestResource
@CrossOrigin("*")
public interface FilmProjectionRepository extends JpaRepository<FilmProjection, Long> {
    List<FilmProjection> findBySalle_Id (Long salleId);

}
