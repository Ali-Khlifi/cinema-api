package fr.uga.gestioncinema.web;

import fr.uga.gestioncinema.dto.SeatAvailabilityDto;
import fr.uga.gestioncinema.mappers.SeatMapper;
import fr.uga.gestioncinema.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.SeatApi;
import org.openapitools.model.SeatOpenApiModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Seat entity operations.
 * Implements the PlaceApi interface generated from OpenAPI specification.
 */
@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class SeatController implements SeatApi {

    private final SeatService service;
    private final SeatMapper mapper;

    @Override
    public ResponseEntity<SeatOpenApiModel> create(SeatOpenApiModel model) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toOpenApiModel(service.save(mapper.toDto(model))));
    }

    @Override
    public ResponseEntity<SeatOpenApiModel> findOne(final Long id) {
        return ResponseEntity.ok()
                .body(mapper.toOpenApiModel(service.fetchOne(id)));
    }

    @Override
    public ResponseEntity<SeatOpenApiModel> update(SeatOpenApiModel model) {
        return ResponseEntity.ok()
                .body(mapper.toOpenApiModel(service.update(mapper.toDto(model))));
    }

    @Override
    public ResponseEntity<List<SeatOpenApiModel>> findAll() {
        return ResponseEntity.ok()
                .body(mapper.toOpenApiModelList(service.fetchAll()));
    }

    @Override
    public ResponseEntity<Void> delete(Long id) throws Exception {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    
    /**
     * Get available seats for a film projection.
     * This endpoint is not part of the standard OpenAPI spec.
     * 
     * @param filmProjectionId the film projection ID
     * @return list of seats with availability status
     */
    @GetMapping("/seat/availableByFilmProjection/{filmProjectionId}")
    public ResponseEntity<List<SeatAvailabilityDto>> getAvailableSeats(
            @PathVariable Long filmProjectionId) {
        return ResponseEntity.ok(service.findAvailableByFilmProjection(filmProjectionId));
    }
}
