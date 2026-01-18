package fr.uga.gestioncinema.web;

import fr.uga.gestioncinema.mappers.CityMapper;
import fr.uga.gestioncinema.service.CityService;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.CityApi;
import org.openapitools.model.CityOpenApiModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for City entity operations.
 * Implements the VilleApi interface generated from OpenAPI specification.
 */
@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class CityController implements CityApi {
    
    private final CityMapper mapper;
    private final CityService service;

    @Override
    public ResponseEntity<CityOpenApiModel> create(CityOpenApiModel model) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toOpenApiModel(service.save(mapper.toDto(model))));
    }

    @Override
    public ResponseEntity<CityOpenApiModel> update(CityOpenApiModel model) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(mapper.toOpenApiModel(service.update(mapper.toDto(model))));
    }

    @Override
    public ResponseEntity<CityOpenApiModel> findOne(Long id) throws Exception {
        return ResponseEntity.ok()
                .body(mapper.toOpenApiModel(service.fetchOne(id)));
    }

    @Override
    public ResponseEntity<List<CityOpenApiModel>> findAll() throws Exception {
        return ResponseEntity.ok()
                .body(mapper.toOpenApiModelList(service.fetchAll()));
    }

    @Override
    public ResponseEntity<Void> delete(Long id) throws Exception {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
