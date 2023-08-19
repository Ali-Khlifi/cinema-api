package fr.uga.gestioncinema.service;

import fr.uga.gestioncinema.dto.PlaceDto;
import jakarta.validation.Valid;

import java.util.List;

public interface PlaceService {
    PlaceDto fetchOne(Long id);
    PlaceDto save(@Valid PlaceDto dto);
    List<PlaceDto> fetchAll();
    PlaceDto update(@Valid PlaceDto dto);
    void delete(Long id);
}
