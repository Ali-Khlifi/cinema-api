package fr.uga.gestioncinema.web;

import fr.uga.gestioncinema.mappers.CinemaMapper;
import fr.uga.gestioncinema.mappers.SeanceMapper;
import fr.uga.gestioncinema.service.CinemaService;
import fr.uga.gestioncinema.service.SeanceService;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.CinemaApi;
import org.openapitools.api.SeanceApi;
import org.openapitools.model.CinemaOpenApiModel;
import org.openapitools.model.SeanceOpenApiModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class SeanceController implements SeanceApi {

    private final SeanceService service;
    private final SeanceMapper mapper;

    @Override
    public ResponseEntity<SeanceOpenApiModel> create(SeanceOpenApiModel model) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                mapper.toOpenApiModel(
                        service.save(
                                mapper.toDto(model))));
    }

    @Override
    public ResponseEntity<SeanceOpenApiModel> findOne(final Long id) {
        return ResponseEntity.ok().body(
                mapper.toOpenApiModel(
                        service.fetchOne(id)));
    }

    @Override
    public ResponseEntity<SeanceOpenApiModel> update(SeanceOpenApiModel model) {
        return ResponseEntity.ok().body(
                mapper.toOpenApiModel(
                        service.update(
                                mapper.toDto(model))));
    }

    @Override
    public ResponseEntity<List<SeanceOpenApiModel>> findAll() {
        return ResponseEntity.ok().body(mapper.toOpenApiModelList(service.fetchAll()));
    }
    @Override
    public ResponseEntity<Void> delete(Long id) throws Exception {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @Override
    public ResponseEntity<List<SeanceOpenApiModel>> findByFilmProjection (Long id){
        return ResponseEntity.ok().body(mapper.toOpenApiModelList(service.findByFilmProjection(id)));
    }
}












