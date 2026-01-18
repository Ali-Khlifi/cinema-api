package fr.uga.gestioncinema.service;

import fr.uga.gestioncinema.dto.CinemaDto;
import fr.uga.gestioncinema.dto.CityDto;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Service interface for Cinema entity operations.
 */
public interface CinemaService {
    CinemaDto fetchOne(Long id);
    CinemaDto save(@Valid CinemaDto dto);
    List<CinemaDto> fetchAll();
    CinemaDto update(@Valid CinemaDto dto);
    List<CinemaDto> findByCity(CityDto dto);
    void delete(Long id);
}
