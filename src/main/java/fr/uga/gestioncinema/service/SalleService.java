package fr.uga.gestioncinema.service;

import fr.uga.gestioncinema.dto.CinemaDto;
import fr.uga.gestioncinema.dto.SalleDto;
import jakarta.validation.Valid;

import java.util.List;

public interface SalleService {
    SalleDto fetchOne(Long id);
    SalleDto save(@Valid SalleDto dto);
    List<SalleDto> fetchAll();
    SalleDto update(@Valid SalleDto dto);
    List<SalleDto> findByCinema(CinemaDto dto);
    void delete(Long id);
}
