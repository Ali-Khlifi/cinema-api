package fr.uga.gestioncinema.repositories;

import fr.uga.gestioncinema.entities.Film;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
@Transactional
@CrossOrigin("*")
public interface FilmRepository extends JpaRepository<Film, Long> {

    Optional<Film> findByTitle(String title);
    Optional<Film> findByTmdbId(Integer tmdbId);

    List<Film> findByCategoryId(Long id);

}
