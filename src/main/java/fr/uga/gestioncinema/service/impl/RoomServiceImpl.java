package fr.uga.gestioncinema.service.impl;

import fr.uga.gestioncinema.dto.CinemaDto;
import fr.uga.gestioncinema.dto.RoomDto;
import fr.uga.gestioncinema.exceptions.CustomErrorService;
import fr.uga.gestioncinema.mappers.RoomMapper;
import fr.uga.gestioncinema.repositories.RoomRepository;
import fr.uga.gestioncinema.repositories.TicketRepository;
import fr.uga.gestioncinema.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for Room entity operations.
 */
@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    
    @Autowired
    CustomErrorService customErrorService;
    
    private final RoomRepository repository;
    private final TicketRepository ticketRepository;
    private final RoomMapper mapper;

    @Override
    public RoomDto fetchOne(Long id) {
        return mapper.toDto(repository.findById(id)
                .orElseThrow(() -> customErrorService.customErrorException("Room not found", HttpStatus.NOT_FOUND)));
    }

    @Override
    public RoomDto save(RoomDto dto) {
        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    @Override
    public List<RoomDto> fetchAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public RoomDto update(RoomDto dto) {
        final var room = repository.findById(dto.getId())
                .orElseThrow(() -> customErrorService.customErrorException("Room not found", HttpStatus.NOT_FOUND));

        mapper.update(dto, room);

        return mapper.toDto(repository.save(room));
    }

    @Override
    public List<RoomDto> findByCinema(CinemaDto dto) {
        return repository.findByCinemaName(dto.getName())
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<RoomDto> findByCinemaId(Long cinemaId) {
        return repository.findByCinemaId(cinemaId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public boolean isFull(Long roomId, Long filmProjectionId) {
        final var room = repository.findById(roomId)
                .orElseThrow(() -> customErrorService.customErrorException("Room not found", HttpStatus.NOT_FOUND));

        final var occupiedSeats = ticketRepository.findByFilmProjectionId(filmProjectionId).size();

        return occupiedSeats >= room.getNumberOfSeats();
    }

    @Override
    public int countAvailableSeats(Long roomId, Long filmProjectionId) {
        final var room = repository.findById(roomId)
                .orElseThrow(() -> customErrorService.customErrorException("Room not found", HttpStatus.NOT_FOUND));

        final var occupiedSeats = ticketRepository.findByFilmProjectionId(filmProjectionId).size();

        return room.getNumberOfSeats() - occupiedSeats;
    }
}
