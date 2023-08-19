package fr.uga.gestioncinema.service;

import fr.uga.gestioncinema.dto.FilmProjectionDto;
import jakarta.validation.Valid;

import java.util.List;

public interface FilmProjectionService {

    FilmProjectionDto fetchOne(Long id);

    FilmProjectionDto save(@Valid FilmProjectionDto dto);
    List<FilmProjectionDto> fetchAll();
    FilmProjectionDto update(@Valid FilmProjectionDto dto);
    List<FilmProjectionDto> findBySalle(Long salleId);

    void delete(Long id);


}
