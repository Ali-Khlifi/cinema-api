package fr.uga.gestioncinema.repositories;

import fr.uga.gestioncinema.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@RepositoryRestResource
@CrossOrigin("*")
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByFilmProjection_Id(Long filmProjectionId);

}
