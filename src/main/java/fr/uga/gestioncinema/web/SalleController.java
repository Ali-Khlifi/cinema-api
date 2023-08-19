package fr.uga.gestioncinema.web;

import fr.uga.gestioncinema.dto.CinemaDto;
import fr.uga.gestioncinema.dto.SalleDto;
import fr.uga.gestioncinema.mappers.CinemaMapper;
import fr.uga.gestioncinema.mappers.SalleMapper;
import fr.uga.gestioncinema.service.CinemaService;
import fr.uga.gestioncinema.service.SalleService;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.CinemaApi;
import org.openapitools.api.SalleApi;
import org.openapitools.model.CinemaOpenApiModel;
import org.openapitools.model.SalleOpenApiModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class SalleController implements SalleApi {

    private final SalleService service;
    private final SalleMapper mapper;

    @Override
    public ResponseEntity<SalleOpenApiModel> create(SalleOpenApiModel model) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                mapper.toOpenApiModel(
                        service.save(
                                mapper.toDto(model))));
    }

    @Override
    public ResponseEntity<SalleOpenApiModel> findOne(final Long id) {
        return ResponseEntity.ok().body(
                mapper.toOpenApiModel(
                        service.fetchOne(id)));
    }

    @Override
    public ResponseEntity<SalleOpenApiModel> update(SalleOpenApiModel model) {
        return ResponseEntity.ok().body(
                mapper.toOpenApiModel(
                        service.update(
                                mapper.toDto(model))));
    }

    @Override
    public ResponseEntity<List<SalleOpenApiModel>> findAll() {
        return ResponseEntity.ok().body(mapper.toOpenApiModelList(service.fetchAll()));
    }
    @Override
    public ResponseEntity<Void> delete(Long id) throws Exception {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @Override
    public ResponseEntity<List<SalleOpenApiModel>> findByCinema(String cinemaName) throws Exception {

        CinemaDto cinemaDto = CinemaDto.builder().build();
        cinemaDto.setName(cinemaName);
        List<SalleDto> salles = service.findByCinema(cinemaDto);
        return ResponseEntity.ok().body(mapper.toOpenApiModelList(salles));

    }

}












