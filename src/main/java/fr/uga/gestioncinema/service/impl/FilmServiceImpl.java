package fr.uga.gestioncinema.service.impl;

import fr.uga.gestioncinema.repositories.FilmRepository;
import fr.uga.gestioncinema.dto.FilmDto;
import fr.uga.gestioncinema.entities.Film;
import fr.uga.gestioncinema.exceptions.NotFoundException;
import fr.uga.gestioncinema.mappers.FilmMapper;
import fr.uga.gestioncinema.service.FilmService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static fr.uga.gestioncinema.utils.StringUtils.Exceptions.NO_DATA;


@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmRepository filmRepository;
    private final FilmMapper filmMapper;


    @Override
    public byte[] readImages(Long id) throws IOException {
        Film f = filmRepository.findById(id).get();
        String photoName = f.getPhoto();
        // Build the file path
        String filePath = "D:/AutoFormations/AutoFormation_2023/Gestion-Cinema/cinema-api/images/" + photoName;
        // Read the file from the filesystem
        Path path = Paths.get(filePath);
        return Files.readAllBytes(path);
    }
    @Override
    @Transactional
    public void saveFilmWithImage(Long id, MultipartFile imageFile) {
        try {
            Film film = filmRepository.findById(id).get();
            String fileName = imageFile.getOriginalFilename();
            Path path = Paths.get("D:/AutoFormations/AutoFormation_2023/Gestion-Cinema/cinema-api/images/" + fileName);
            Files.write(path, imageFile.getBytes());
            film.setPhoto(fileName);
            filmRepository.save(film);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public FilmDto fetchOne(Long id) {
        return filmMapper.toDto(filmRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NO_DATA)));
    }

    @Override
    public FilmDto save(FilmDto dto) {
        return filmMapper.toDto(filmRepository.save(filmMapper.toEntity(dto)));
    }

    @Override
    public List<FilmDto> fetchAll() {
        return filmRepository.findAll()
                .stream()
                .map(filmMapper::toDto)
                .toList();
    }

    @Override
    public FilmDto update(FilmDto dto) {
        final var film = filmRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException(NO_DATA));

        filmMapper.update(dto, film);

        return filmMapper.toDto(filmRepository.save(film));
    }

    @Override
    public void delete(Long id) {
        filmRepository.deleteById(id);

    }

}

