package fr.uga.gestioncinema.repositories;

import fr.uga.gestioncinema.entities.Seance;
import fr.uga.gestioncinema.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@RepositoryRestResource   // Spring data Rest
// toutes les méthodes hérités de JpaRepository sont accessibles via une API REST
@CrossOrigin("*")
public interface SeanceRepository extends JpaRepository<Seance, Long> {

    List<Seance> findByFilmProjection_Id(Long filmProjectionId);

}
