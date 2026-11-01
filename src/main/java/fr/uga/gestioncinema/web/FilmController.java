package fr.uga.gestioncinema.web;

import fr.uga.gestioncinema.mappers.FilmMapper;
import fr.uga.gestioncinema.repositories.FilmRepository;
import fr.uga.gestioncinema.service.FilmService;
import fr.uga.gestioncinema.service.PosterUploadService;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.FilmApi;
import org.openapitools.model.FilmOpenApiModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class FilmController implements FilmApi {

    private final FilmService filmService;
    private final FilmMapper filmMapper;
    private final FilmRepository filmRepository;
    private final PosterUploadService posterUploadService;

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

    @PostMapping(path = "/films/{id}/poster", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadPoster(@PathVariable("id") Long id,
                                             @RequestParam("file") MultipartFile file) {
        posterUploadService.upload(id, file);
        return ResponseEntity.noContent().build();
    }

    // upload media for a film
    @Override
    public ResponseEntity<Void> uploadMedia (Long idFilm, MultipartFile file) {
        try {
            filmService.saveMedia(idFilm, file);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // read media film
    @Override
    public ResponseEntity<byte []> readMedia (Long id) {
        try {
            byte [] media = filmService.getMediaFilm(id);
            if(media == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            String mediaType = filmService.determineMediaType(media);
            MediaType contentType = mediaType != null ?
                    MediaType.parseMediaType(mediaType) :
                    MediaType.APPLICATION_OCTET_STREAM;


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(contentType);
            headers.setContentLength(media.length);


            return ResponseEntity.ok()
                    .headers(headers)
                    .body(media);

        }catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<Void> delete(Long id) throws Exception {
        filmService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}












