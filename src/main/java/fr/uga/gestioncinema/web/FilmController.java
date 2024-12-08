package fr.uga.gestioncinema.web;

import fr.uga.gestioncinema.entities.Film;
import fr.uga.gestioncinema.mappers.FilmMapper;
import fr.uga.gestioncinema.repositories.FilmRepository;
import fr.uga.gestioncinema.service.FilmService;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.FilmApi;
import org.openapitools.model.FilmOpenApiModel;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class FilmController implements FilmApi {

    private final FilmService filmService;
    private final FilmMapper filmMapper;
    private final FilmRepository filmRepository;

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
    @Override
    public ResponseEntity<org.springframework.core.io.Resource> readMedia(Long id) {
        try {
            // Rechercher le film
            Optional<Film> film = filmRepository.findById(id);
            System.out.println(film);
            if (film.isEmpty() || film.get().getMedia() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // Récupérer les données média
            byte[] media = film.get().getMedia();

            // Convertir les données en org.springframework.core.io.Resource
            ByteArrayResource resource = new ByteArrayResource(media);

            // Retourner les données avec les headers appropriés
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(media.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @Override
    public ResponseEntity<Void> delete(Long id) throws Exception {
        filmService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}












