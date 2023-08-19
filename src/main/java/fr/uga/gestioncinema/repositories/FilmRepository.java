package fr.uga.gestioncinema.repositories;

import fr.uga.gestioncinema.entities.Film;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

@RepositoryRestResource
@Transactional
@CrossOrigin("*")
public interface FilmRepository extends JpaRepository<Film, Long> {

}
