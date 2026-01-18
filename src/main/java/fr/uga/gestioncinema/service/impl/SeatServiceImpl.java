package fr.uga.gestioncinema.service.impl;

import fr.uga.gestioncinema.dto.SeatAvailabilityDto;
import fr.uga.gestioncinema.dto.SeatDto;
import fr.uga.gestioncinema.entities.FilmProjection;
import fr.uga.gestioncinema.exceptions.CustomErrorService;
import fr.uga.gestioncinema.mappers.SeatMapper;
import fr.uga.gestioncinema.repositories.FilmProjectionRepository;
import fr.uga.gestioncinema.repositories.SeatRepository;
import fr.uga.gestioncinema.repositories.TicketRepository;
import fr.uga.gestioncinema.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service implementation for Seat entity operations.
 */
@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {
    
    @Autowired
    CustomErrorService customErrorService;
    
    private final SeatRepository repository;
    private final SeatMapper mapper;
    private final FilmProjectionRepository filmProjectionRepository;
    private final TicketRepository ticketRepository;

    @Override
    public SeatDto fetchOne(Long id) {
        return mapper.toDto(repository.findById(id)
                .orElseThrow(() -> customErrorService.customErrorException("Seat not found", HttpStatus.NOT_FOUND)));
    }

    @Override
    public SeatDto save(SeatDto dto) {
        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    @Override
    public List<SeatDto> fetchAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public SeatDto update(SeatDto dto) {
        final var seat = repository.findById(dto.getId())
                .orElseThrow(() -> customErrorService.customErrorException("Seat not found", HttpStatus.NOT_FOUND));

        mapper.update(dto, seat);

        return mapper.toDto(repository.save(seat));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<SeatAvailabilityDto> findAvailableByFilmProjection(Long filmProjectionId) {
        // Get the film projection to know which room
        FilmProjection projection = filmProjectionRepository.findById(filmProjectionId)
                .orElseThrow(() -> customErrorService.customErrorException("Film projection not found", HttpStatus.NOT_FOUND));
        
        Long roomId = projection.getRoom().getId();
        
        // Get all seats in the room
        List<fr.uga.gestioncinema.entities.Seat> allSeats = repository.findByRoomId(roomId);
        
        // Get reserved seat IDs for this projection
        Set<Long> reservedSeatIds = ticketRepository.findByFilmProjectionId(filmProjectionId)
                .stream()
                .map(ticket -> ticket.getSeat().getId())
                .collect(Collectors.toSet());
        
        // Default base price (could be configurable)
        BigDecimal defaultBasePrice = new BigDecimal("10.00");
        
        // Map to availability DTOs
        return allSeats.stream()
                .map(seat -> SeatAvailabilityDto.builder()
                        .seatId(seat.getId())
                        .number(seat.getNumber())
                        .isReserved(reservedSeatIds.contains(seat.getId()))
                        .basePrice(defaultBasePrice)
                        .roomId(roomId)
                        .build())
                .toList();
    }
}
