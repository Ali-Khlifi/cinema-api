package fr.uga.gestioncinema.web;

import fr.uga.gestioncinema.dto.CreateReservationRequest;
import fr.uga.gestioncinema.dto.CreateReservationResponse;
import fr.uga.gestioncinema.mappers.ReservationMapper;
import fr.uga.gestioncinema.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.ReservationApi;
import org.openapitools.model.ReservationOpenApiModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Reservation entity operations.
 * Implements the ReservationApi interface generated from OpenAPI specification.
 */
@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class ReservationController implements ReservationApi {
    
    private final ReservationMapper mapper;
    private final ReservationService service;

    @Override
    public ResponseEntity<ReservationOpenApiModel> create(ReservationOpenApiModel model) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toOpenApiModel(service.save(mapper.toDto(model))));
    }

    @Override
    public ResponseEntity<ReservationOpenApiModel> update(ReservationOpenApiModel model) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(mapper.toOpenApiModel(service.update(mapper.toDto(model))));
    }

    @Override
    public ResponseEntity<ReservationOpenApiModel> findOne(Long id) throws Exception {
        return ResponseEntity.ok()
                .body(mapper.toOpenApiModel(service.fetchOne(id)));
    }

    @Override
    public ResponseEntity<List<ReservationOpenApiModel>> findAll() throws Exception {
        return ResponseEntity.ok()
                .body(mapper.toOpenApiModelList(service.fetchAll()));
    }

    @Override
    public ResponseEntity<List<ReservationOpenApiModel>> findByUserId(Long userId) throws Exception {
        return ResponseEntity.ok()
                .body(mapper.toOpenApiModelList(service.findByUserId(userId)));
    }

    @Override
    public ResponseEntity<Void> delete(Long id) throws Exception {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<Void> cancelReservation(Long id, String reason) throws Exception {
        service.cancelReservation(id, reason);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> confirmReservation(Long id) throws Exception {
        service.confirmReservation(id);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Create a new reservation with tickets.
     * Custom endpoint not part of the standard OpenAPI spec.
     * 
     * @param request the reservation request
     * @return the created reservation response
     */
    @PostMapping("/reservation/create")
    public ResponseEntity<CreateReservationResponse> createReservation(
            @RequestBody CreateReservationRequest request) {
        CreateReservationResponse response = service.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}








