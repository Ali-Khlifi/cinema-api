package fr.uga.gestioncinema.repositories;

import fr.uga.gestioncinema.entities.PriceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PriceTypeRepository extends JpaRepository<PriceType, Long> {
    
    Optional<PriceType> findByCode(String code);
    
    List<PriceType> findByIsActiveTrue();
}











