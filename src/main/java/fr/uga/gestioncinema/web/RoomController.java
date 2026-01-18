package fr.uga.gestioncinema.web;

import fr.uga.gestioncinema.dto.CinemaDto;
import fr.uga.gestioncinema.dto.RoomDto;
import fr.uga.gestioncinema.mappers.RoomMapper;
import fr.uga.gestioncinema.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.RoomApi;
import org.openapitools.model.RoomOpenApiModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for Room entity operations.
 * Implements the SalleApi interface generated from OpenAPI specification.
 */
@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class RoomController implements RoomApi {

    private final RoomService service;
    private final RoomMapper mapper;

    @Override
    public ResponseEntity<RoomOpenApiModel> create(RoomOpenApiModel model) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toOpenApiModel(service.save(mapper.toDto(model))));
    }

    @Override
    public ResponseEntity<RoomOpenApiModel> findOne(final Long id) {
        return ResponseEntity.ok()
                .body(mapper.toOpenApiModel(service.fetchOne(id)));
    }

    @Override
    public ResponseEntity<RoomOpenApiModel> update(RoomOpenApiModel model) {
        return ResponseEntity.ok()
                .body(mapper.toOpenApiModel(service.update(mapper.toDto(model))));
    }

    @Override
    public ResponseEntity<List<RoomOpenApiModel>> findAll() {
        return ResponseEntity.ok()
                .body(mapper.toOpenApiModelList(service.fetchAll()));
    }

    @Override
    public ResponseEntity<Void> delete(Long id) throws Exception {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<List<RoomOpenApiModel>> findByCinema(String cinemaName) throws Exception {
        CinemaDto cinemaDto = CinemaDto.builder().build();
        cinemaDto.setName(cinemaName);
        List<RoomDto> rooms = service.findByCinema(cinemaDto);
        return ResponseEntity.ok()
                .body(mapper.toOpenApiModelList(rooms));
    }

    @Override
    public ResponseEntity<List<RoomOpenApiModel>> findByCinemaId(Long cinemaId) throws Exception {
        List<RoomDto> rooms = service.findByCinemaId(cinemaId);
        return ResponseEntity.ok()
                .body(mapper.toOpenApiModelList(rooms));
    }
}
