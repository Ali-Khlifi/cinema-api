package fr.uga.gestioncinema.web;

import fr.uga.gestioncinema.dto.CinemaDto;
import fr.uga.gestioncinema.dto.CityDto;
import fr.uga.gestioncinema.mappers.CinemaMapper;
import fr.uga.gestioncinema.service.CinemaService;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.CinemaApi;
import org.openapitools.model.CinemaOpenApiModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for Cinema entity operations.
 * Implements the CinemaApi interface generated from OpenAPI specification.
 */
@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class CinemaController implements CinemaApi {

    private final CinemaService service;
    private final CinemaMapper mapper;

    @Override
    public ResponseEntity<CinemaOpenApiModel> create(CinemaOpenApiModel model) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toOpenApiModel(service.save(mapper.toDto(model))));
    }

    @Override
    public ResponseEntity<CinemaOpenApiModel> findOne(final Long id) {
        return ResponseEntity.ok()
                .body(mapper.toOpenApiModel(service.fetchOne(id)));
    }

    @Override
    public ResponseEntity<CinemaOpenApiModel> update(CinemaOpenApiModel model) {
        return ResponseEntity.ok()
                .body(mapper.toOpenApiModel(service.update(mapper.toDto(model))));
    }

    @Override
    public ResponseEntity<List<CinemaOpenApiModel>> findAll() {
        return ResponseEntity.ok()
                .body(mapper.toOpenApiModelList(service.fetchAll()));
    }

    @Override
    public ResponseEntity<Void> delete(Long id) throws Exception {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<List<CinemaOpenApiModel>> findByCity(String cityName) throws Exception {
        CityDto cityDto = CityDto.builder().name(cityName).build();
        List<CinemaDto> cinemas = service.findByCity(cityDto);
        return ResponseEntity.ok()
                .body(mapper.toOpenApiModelList(cinemas));
    }
}
