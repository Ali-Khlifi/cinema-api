package fr.uga.gestioncinema.service;

import fr.uga.gestioncinema.dto.FilmProjectionDto;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface FilmProjectionService {

    FilmProjectionDto fetchOne(Long id);

    FilmProjectionDto save(@Valid FilmProjectionDto dto);
    List<FilmProjectionDto> fetchAll();
    FilmProjectionDto update(@Valid FilmProjectionDto dto);
    List<FilmProjectionDto> findByRoom(Long roomId);

    void delete(Long id);
    List<FilmProjectionDto> findByDate(LocalDate date);

    List<FilmProjectionDto> findUpcoming(LocalDate fromDate, LocalTime fromTime);

    boolean isOngoing(Long id, LocalDate date, LocalTime time);



}
