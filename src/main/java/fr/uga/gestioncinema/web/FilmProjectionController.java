package fr.uga.gestioncinema.web;

import fr.uga.gestioncinema.mappers.CinemaMapper;
import fr.uga.gestioncinema.mappers.FilmProjectionMapper;
import fr.uga.gestioncinema.service.CinemaService;
import fr.uga.gestioncinema.service.FilmProjectionService;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.CinemaApi;
import org.openapitools.api.FilmProjectionApi;
import org.openapitools.model.CinemaOpenApiModel;
import org.openapitools.model.FilmProjectionOpenApiModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class FilmProjectionController implements FilmProjectionApi {

    private final FilmProjectionService service;
    private final FilmProjectionMapper mapper;

    @Override
    public ResponseEntity<FilmProjectionOpenApiModel> create(FilmProjectionOpenApiModel model) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                mapper.toOpenApiModel(
                        service.save(
                                mapper.toDto(model))));
    }

    @Override
    public ResponseEntity<FilmProjectionOpenApiModel> findOne(final Long id) {
        return ResponseEntity.ok().body(
                mapper.toOpenApiModel(
                        service.fetchOne(id)));
    }

    @Override
    public ResponseEntity<FilmProjectionOpenApiModel> update(FilmProjectionOpenApiModel model) {
        return ResponseEntity.ok().body(
                mapper.toOpenApiModel(
                        service.update(
                                mapper.toDto(model))));
    }

    @Override
    public ResponseEntity<List<FilmProjectionOpenApiModel>> findAll() {
        return ResponseEntity.ok().body(mapper.toOpenApiModelList(service.fetchAll()));
    }
    @Override
    public ResponseEntity<Void> delete(Long id) throws Exception {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @Override
    public ResponseEntity<List<FilmProjectionOpenApiModel>> findBySalle(Long id) throws Exception {
        return ResponseEntity.ok().body(mapper.toOpenApiModelList(service.findBySalle(id)));
    }

}












