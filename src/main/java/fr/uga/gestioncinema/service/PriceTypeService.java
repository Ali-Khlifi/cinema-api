package fr.uga.gestioncinema.service;

import fr.uga.gestioncinema.dto.PriceTypeDto;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Service interface for PriceType entity operations.
 */
public interface PriceTypeService {
    PriceTypeDto save(@Valid PriceTypeDto dto);
    PriceTypeDto update(@Valid PriceTypeDto dto);
    List<PriceTypeDto> fetchAll();
    List<PriceTypeDto> fetchAllActive();
    PriceTypeDto fetchOne(Long id);
    PriceTypeDto findByCode(String code);
    void delete(Long id);
}









