package fr.uga.gestioncinema.repositories;

import fr.uga.gestioncinema.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByKeycloakId(String keycloakId);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByKeycloakId(String keycloakId);
    
    boolean existsByEmail(String email);
}











