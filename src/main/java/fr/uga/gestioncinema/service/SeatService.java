package fr.uga.gestioncinema.service;

import fr.uga.gestioncinema.dto.SeatAvailabilityDto;
import fr.uga.gestioncinema.dto.SeatDto;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Service interface for Seat entity operations.
 */
public interface SeatService {
    SeatDto fetchOne(Long id);
    SeatDto save(@Valid SeatDto dto);
    List<SeatDto> fetchAll();
    SeatDto update(@Valid SeatDto dto);
    void delete(Long id);
    
    /**
     * Find all seats for a specific film projection with their availability status.
     * @param filmProjectionId the film projection ID
     * @return list of seats with availability
     */
    List<SeatAvailabilityDto> findAvailableByFilmProjection(Long filmProjectionId);
}
