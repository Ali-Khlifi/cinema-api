package fr.uga.gestioncinema.web;

import fr.uga.gestioncinema.mappers.PlaceMapper;
import fr.uga.gestioncinema.mappers.SalleMapper;
import fr.uga.gestioncinema.service.PlaceService;
import fr.uga.gestioncinema.service.SalleService;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.PlaceApi;
import org.openapitools.api.SalleApi;
import org.openapitools.model.PlaceOpenApiModel;
import org.openapitools.model.SalleOpenApiModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class PlaceController implements PlaceApi {

    private final PlaceService service;
    private final PlaceMapper mapper;

    @Override
    public ResponseEntity<PlaceOpenApiModel> create(PlaceOpenApiModel model) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                mapper.toOpenApiModel(
                        service.save(
                                mapper.toDto(model))));
    }

    @Override
    public ResponseEntity<PlaceOpenApiModel> findOne(final Long id) {
        return ResponseEntity.ok().body(
                mapper.toOpenApiModel(
                        service.fetchOne(id)));
    }

    @Override
    public ResponseEntity<PlaceOpenApiModel> update(PlaceOpenApiModel model) {
        return ResponseEntity.ok().body(
                mapper.toOpenApiModel(
                        service.update(
                                mapper.toDto(model))));
    }

    @Override
    public ResponseEntity<List<PlaceOpenApiModel>> findAll() {
        return ResponseEntity.ok().body(mapper.toOpenApiModelList(service.fetchAll()));
    }
    @Override
    public ResponseEntity<Void> delete(Long id) throws Exception {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}












