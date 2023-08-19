package fr.uga.gestioncinema.repositories;

import fr.uga.gestioncinema.entities.Ville;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

@RepositoryRestResource
public interface VilleRepository extends JpaRepository<Ville, Long> {

}
