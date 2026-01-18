package fr.uga.gestioncinema.service.impl;

import fr.uga.gestioncinema.dto.CreateReservationRequest;
import fr.uga.gestioncinema.dto.CreateReservationResponse;
import fr.uga.gestioncinema.dto.ReservationDto;
import fr.uga.gestioncinema.entities.*;
import fr.uga.gestioncinema.exceptions.CustomErrorService;
import fr.uga.gestioncinema.mappers.ReservationMapper;
import fr.uga.gestioncinema.repositories.*;
import fr.uga.gestioncinema.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for Reservation entity operations.
 */
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    CustomErrorService customErrorService;

    private final ReservationRepository repository;
    private final ReservationMapper mapper;
    private final FilmProjectionRepository filmProjectionRepository;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;
    private final PriceTypeRepository priceTypeRepository;
    private final UserRepository userRepository;

    @Override
    public ReservationDto fetchOne(Long id) {
        return mapper.toDto(repository.findById(id)
                .orElseThrow(() -> customErrorService.customErrorException("Reservation not found", HttpStatus.NOT_FOUND)));
    }

    @Override
    public ReservationDto findByReferenceNumber(String referenceNumber) {
        return mapper.toDto(repository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> customErrorService.customErrorException("Reservation not found with reference: " + referenceNumber, HttpStatus.NOT_FOUND)));
    }

    @Override
    public List<ReservationDto> findByUserId(Long userId) {
        return repository.findByUserId(userId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<ReservationDto> findByStatus(String status) {
        return repository.findByStatus(status)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void cancelReservation(Long id, String reason) {
        final var reservation = repository.findById(id)
                .orElseThrow(() -> customErrorService.customErrorException("Reservation not found", HttpStatus.NOT_FOUND));
        
        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        reservation.setCancellationReason(reason);
        reservation.setCancellationDate(LocalDateTime.now());
        
        repository.save(reservation);
    }

    @Override
    public void confirmReservation(Long id) {
        final var reservation = repository.findById(id)
                .orElseThrow(() -> customErrorService.customErrorException("Reservation not found", HttpStatus.NOT_FOUND));
        
        reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
        reservation.setConfirmationDate(LocalDateTime.now());
        
        repository.save(reservation);
    }

    @Override
    public ReservationDto save(ReservationDto dto) {
        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    @Override
    public ReservationDto update(@Valid final ReservationDto dto) {
        final var reservation = repository.findById(dto.getId())
                .orElseThrow(() -> customErrorService.customErrorException("Reservation not found", HttpStatus.NOT_FOUND));

        mapper.update(dto, reservation);
        repository.save(reservation);

        return mapper.toDto(reservation);
    }

    @Override
    public List<ReservationDto> fetchAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public CreateReservationResponse createReservation(CreateReservationRequest request) {
        // Validation
        if (request.getSeatIds() == null || request.getSeatIds().isEmpty()) {
            throw customErrorService.customErrorException("At least one seat must be selected", HttpStatus.BAD_REQUEST);
        }
        
        if (request.getTermsAccepted() == null || !request.getTermsAccepted()) {
            throw customErrorService.customErrorException("Terms and conditions must be accepted", HttpStatus.BAD_REQUEST);
        }
        
        // Get current user from JWT token (create if doesn't exist)
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String keycloakId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");
        
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseGet(() -> {
                    // Create new user if not exists
                    User newUser = new User();
                    newUser.setKeycloakId(keycloakId);
                    newUser.setEmail(email != null ? email : keycloakId + "@cinema.com");
                    newUser.setFirstName(firstName);
                    newUser.setLastName(lastName);
                    newUser.setIsActive(true);
                    return userRepository.save(newUser);
                });
        
        // Get film projection
        FilmProjection projection = filmProjectionRepository.findById(request.getFilmProjectionId())
                .orElseThrow(() -> customErrorService.customErrorException("Film projection not found", HttpStatus.NOT_FOUND));
        
        // Get seats and verify they exist and belong to the correct room
        List<Seat> seats = new ArrayList<>();
        for (Long seatId : request.getSeatIds()) {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> customErrorService.customErrorException("Seat not found: " + seatId, HttpStatus.NOT_FOUND));
            
            // Verify seat belongs to the projection's room
            if (!seat.getRoom().getId().equals(projection.getRoom().getId())) {
                throw customErrorService.customErrorException("Seat " + seatId + " does not belong to this room", HttpStatus.BAD_REQUEST);
            }
            
            seats.add(seat);
        }
        
        // Check if seats are already reserved for this projection
        List<Ticket> existingTickets = ticketRepository.findByFilmProjectionId(request.getFilmProjectionId());
        List<Long> reservedSeatIds = existingTickets.stream()
                .map(t -> t.getSeat().getId())
                .collect(Collectors.toList());
        
        for (Long seatId : request.getSeatIds()) {


            if (reservedSeatIds.contains(seatId)) {
                throw customErrorService.customErrorException("Seat " + seatId + " is already reserved", HttpStatus.CONFLICT);
            }
        }
        
        // Get price type (default to NORMAL if not specified)
        Long priceTypeId = request.getPriceTypeId() != null ? request.getPriceTypeId() : 1L;
        PriceType priceType = priceTypeRepository.findById(priceTypeId)
                .orElseThrow(() -> customErrorService.customErrorException("Price type not found", HttpStatus.NOT_FOUND));
        
        // Calculate prices
        BigDecimal basePrice = new BigDecimal("10.00"); // Default base price
        BigDecimal discountPercentage = priceType.getDiscountPercentage();
        BigDecimal finalPricePerSeat = basePrice.multiply(BigDecimal.ONE.subtract(discountPercentage.divide(new BigDecimal("100"))));
        BigDecimal totalAmount = finalPricePerSeat.multiply(new BigDecimal(seats.size()));
        
        // Create reservation
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setFilmProjection(projection);
        reservation.setStatus(Reservation.ReservationStatus.PENDING);
        reservation.setSubtotal(totalAmount);
        reservation.setTotalAmount(totalAmount);
        reservation.setTermsAccepted(request.getTermsAccepted());
        reservation.setMarketingConsent(request.getMarketingConsent() != null ? request.getMarketingConsent() : false);
        reservation.setReservationDate(LocalDateTime.now());
        reservation.setExpirationDate(LocalDateTime.now().plusMinutes(15)); // 15 minutes to complete payment
        
        reservation = repository.save(reservation);
        
        // Create tickets for each seat
        List<Integer> seatNumbers = new ArrayList<>();
        for (Seat seat : seats) {
            Ticket ticket = new Ticket();
            ticket.setReservation(reservation);
            ticket.setSeat(seat);
            ticket.setPriceType(priceType);
            ticket.setBasePrice(basePrice);
            ticket.setFinalPrice(finalPricePerSeat);
            ticket.setIsUsed(false);
            
            ticketRepository.save(ticket);
            seatNumbers.add(seat.getNumber());
        }
        
        // Build response
        return CreateReservationResponse.builder()
                .reservationId(reservation.getId())
                .referenceNumber(reservation.getReferenceNumber())
                .totalAmount(totalAmount)
                .ticketCount(seats.size())
                .seatNumbers(seatNumbers)
                .status(reservation.getStatus().name())
                .message("Reservation created successfully. Please complete payment within 15 minutes.")
                .build();
    }
}

