package fr.uga.gestioncinema.web;

import fr.uga.gestioncinema.mappers.PriceTypeMapper;
import fr.uga.gestioncinema.service.PriceTypeService;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.PriceTypeApi;
import org.openapitools.model.PriceTypeOpenApiModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for PriceType entity operations.
 * Implements the PriceTypeApi interface generated from OpenAPI specification.
 */
@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class PriceTypeController implements PriceTypeApi {
    
    private final PriceTypeMapper mapper;
    private final PriceTypeService service;

    @Override
    public ResponseEntity<PriceTypeOpenApiModel> create(PriceTypeOpenApiModel model) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toOpenApiModel(service.save(mapper.toDto(model))));
    }

    @Override
    public ResponseEntity<PriceTypeOpenApiModel> update(PriceTypeOpenApiModel model) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(mapper.toOpenApiModel(service.update(mapper.toDto(model))));
    }

    @Override
    public ResponseEntity<PriceTypeOpenApiModel> findOne(Long id) throws Exception {
        return ResponseEntity.ok()
                .body(mapper.toOpenApiModel(service.fetchOne(id)));
    }

    @Override
    public ResponseEntity<List<PriceTypeOpenApiModel>> findAll() throws Exception {
        return ResponseEntity.ok()
                .body(mapper.toOpenApiModelList(service.fetchAll()));
    }

    @Override
    public ResponseEntity<List<PriceTypeOpenApiModel>> findAllActive() throws Exception {
        return ResponseEntity.ok()
                .body(mapper.toOpenApiModelList(service.fetchAllActive()));
    }

    @Override
    public ResponseEntity<Void> delete(Long id) throws Exception {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}









