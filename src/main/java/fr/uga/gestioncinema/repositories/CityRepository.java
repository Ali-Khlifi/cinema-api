package fr.uga.gestioncinema.repositories;

import fr.uga.gestioncinema.entities.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository for City entity.
 */
@RepositoryRestResource
public interface CityRepository extends JpaRepository<City, Long> {
}
