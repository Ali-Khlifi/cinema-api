package fr.uga.gestioncinema.web;

import fr.uga.gestioncinema.dto.CinemaDto;
import fr.uga.gestioncinema.dto.VilleDto;
import fr.uga.gestioncinema.mappers.CinemaMapper;
import fr.uga.gestioncinema.mappers.FilmMapper;
import fr.uga.gestioncinema.service.CinemaService;
import fr.uga.gestioncinema.service.FilmService;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.CinemaApi;
import org.openapitools.api.FilmApi;
import org.openapitools.model.CinemaOpenApiModel;
import org.openapitools.model.FilmOpenApiModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class CinemaController implements CinemaApi {

    private final CinemaService service;
    private final CinemaMapper mapper;

    @Override
    public ResponseEntity<CinemaOpenApiModel> create(CinemaOpenApiModel model) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                mapper.toOpenApiModel(
                        service.save(
                                mapper.toDto(model))));
    }

    @Override
    public ResponseEntity<CinemaOpenApiModel> findOne(final Long id) {
        return ResponseEntity.ok().body(
                mapper.toOpenApiModel(
                        service.fetchOne(id)));
    }

    @Override
    public ResponseEntity<CinemaOpenApiModel> update(CinemaOpenApiModel model) {
        return ResponseEntity.ok().body(
                mapper.toOpenApiModel(
                        service.update(
                                mapper.toDto(model))));
    }

    @Override
    public ResponseEntity<List<CinemaOpenApiModel>> findAll() {
        return ResponseEntity.ok().body(mapper.toOpenApiModelList(service.fetchAll()));
    }
    @Override
    public ResponseEntity<Void> delete(Long id) throws Exception {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @Override
    public ResponseEntity<List<CinemaOpenApiModel>> findByVille(String villeName) throws Exception {
        VilleDto villeDto = VilleDto.builder().build();
        villeDto.setName(villeName);
        List<CinemaDto> cinemas = service.findByVille(villeDto);
        return ResponseEntity.ok().body(mapper.toOpenApiModelList(cinemas));
    }

}












