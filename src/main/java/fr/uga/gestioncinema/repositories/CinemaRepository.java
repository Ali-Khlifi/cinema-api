package fr.uga.gestioncinema.repositories;

import fr.uga.gestioncinema.dto.CinemaDto;
import fr.uga.gestioncinema.entities.Cinema;
import fr.uga.gestioncinema.entities.Ville;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;


@CrossOrigin("*")
public interface CinemaRepository extends JpaRepository<Cinema, Long> {
    List<Cinema> findByVilleName (String villeName);

}
