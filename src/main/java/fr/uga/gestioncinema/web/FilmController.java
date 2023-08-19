package fr.uga.gestioncinema.web;

import fr.uga.gestioncinema.mappers.FilmMapper;
import fr.uga.gestioncinema.service.FilmService;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.FilmApi;
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
public class FilmController implements FilmApi {

    private final FilmService filmService;
    private final FilmMapper filmMapper;

    @Override
    public ResponseEntity<FilmOpenApiModel> create(FilmOpenApiModel model) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                filmMapper.toOpenApiModel(
                        filmService.save(
                                filmMapper.toDto(model))));
    }

    @Override
    public ResponseEntity<FilmOpenApiModel> findOne(final Long id) {
        return ResponseEntity.ok().body(
                filmMapper.toOpenApiModel(
                        filmService.fetchOne(id)));
    }

    @Override
    public ResponseEntity<FilmOpenApiModel> update(FilmOpenApiModel model) {
        return ResponseEntity.ok().body(
                filmMapper.toOpenApiModel(
                        filmService.update(
                                filmMapper.toDto(model))));
    }

    @Override
    public ResponseEntity<List<FilmOpenApiModel>> findAll() {
        return ResponseEntity.ok().body(filmMapper.toOpenApiModelList(filmService.fetchAll()));
    }

    // upload a new image for a film
     @Override
    public ResponseEntity<Void> uploadImage(Long id, MultipartFile file) {
        try {
            filmService.saveFilmWithImage(id, file);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
     /* @PostMapping(path = "/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("id")Long id, @RequestParam("photo") MultipartFile file) {
        try {
            filmService.saveFilmWithImage(id, file);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
      }*/

    @Override
    public ResponseEntity<byte[]> readImages(Long id) throws IOException {
        byte[] image = filmService.readImages(id);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }
    /* @GetMapping(path = "/imageFilm/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> readImages(@PathVariable(name = "id")Long id) throws IOException {
        byte[] image = filmService.readImages(id);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }*/

    @Override
    public ResponseEntity<Void> delete(Long id) throws Exception {
        filmService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}












