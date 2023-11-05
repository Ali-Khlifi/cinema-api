package fr.uga.gestioncinema.service;

import fr.uga.gestioncinema.dto.CinemaDto;
import fr.uga.gestioncinema.dto.SeanceDto;
import jakarta.validation.Valid;

import java.util.List;

public interface SeanceService {
    SeanceDto fetchOne(Long id);
    SeanceDto save(@Valid SeanceDto dto);
    List<SeanceDto> fetchAll();
    SeanceDto update(@Valid SeanceDto dto);
    List<SeanceDto>findByFilmProjection(Long filmProjectionId);

    void delete(Long id);
}
