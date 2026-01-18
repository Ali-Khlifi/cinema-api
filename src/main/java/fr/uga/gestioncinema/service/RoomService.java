package fr.uga.gestioncinema.service;

import fr.uga.gestioncinema.dto.CinemaDto;
import fr.uga.gestioncinema.dto.RoomDto;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Service interface for Room entity operations.
 */
public interface RoomService {
    RoomDto fetchOne(Long id);
    RoomDto save(@Valid RoomDto dto);
    List<RoomDto> fetchAll();
    RoomDto update(@Valid RoomDto dto);
    List<RoomDto> findByCinema(CinemaDto dto);
    List<RoomDto> findByCinemaId(Long cinemaId);
    void delete(Long id);
}
