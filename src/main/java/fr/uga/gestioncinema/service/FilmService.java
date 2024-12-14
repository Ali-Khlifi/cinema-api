package fr.uga.gestioncinema.service;

import fr.uga.gestioncinema.dto.FilmDto;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FilmService {

    FilmDto fetchOne(Long id);

    FilmDto save(@Valid FilmDto dto);
    List<FilmDto> fetchAll();
    FilmDto update(@Valid FilmDto dto);

    void delete(Long id);
    void saveMedia(Long idFilm, MultipartFile file);

    byte[] getMediaFilm(Long id);

    String determineMediaType(byte[] media);
}
