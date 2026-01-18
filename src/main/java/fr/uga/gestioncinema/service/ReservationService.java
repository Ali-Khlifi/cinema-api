package fr.uga.gestioncinema.service;

import fr.uga.gestioncinema.dto.CreateReservationRequest;
import fr.uga.gestioncinema.dto.CreateReservationResponse;
import fr.uga.gestioncinema.dto.ReservationDto;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Service interface for Reservation entity operations.
 */
public interface ReservationService {
    ReservationDto save(@Valid ReservationDto dto);
    ReservationDto update(@Valid ReservationDto dto);
    List<ReservationDto> fetchAll();
    ReservationDto fetchOne(Long id);
    ReservationDto findByReferenceNumber(String referenceNumber);
    List<ReservationDto> findByUserId(Long userId);
    List<ReservationDto> findByStatus(String status);
    void delete(Long id);
    void cancelReservation(Long id, String reason);
    void confirmReservation(Long id);
    
    /**
     * Create a new reservation with tickets.
     * @param request the reservation request
     * @return the created reservation response
     */
    CreateReservationResponse createReservation(CreateReservationRequest request);
}