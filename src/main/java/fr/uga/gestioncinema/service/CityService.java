package fr.uga.gestioncinema.service;

import fr.uga.gestioncinema.dto.CityDto;

import javax.validation.Valid;
import java.util.List;

/**
 * Service interface for City entity operations.
 */
public interface CityService {
    CityDto save(@Valid CityDto dto);
    CityDto update(@Valid CityDto dto);
    List<CityDto> fetchAll();
    CityDto fetchOne(Long id);
    void delete(Long id);
}
