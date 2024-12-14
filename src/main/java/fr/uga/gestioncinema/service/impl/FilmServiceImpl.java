package fr.uga.gestioncinema.service.impl;

import fr.uga.gestioncinema.dto.FilmDto;
import fr.uga.gestioncinema.entities.Film;
import fr.uga.gestioncinema.exceptions.NotFoundException;
import fr.uga.gestioncinema.mappers.FilmMapper;
import fr.uga.gestioncinema.repositories.FilmRepository;
import fr.uga.gestioncinema.service.FilmService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;

import static fr.uga.gestioncinema.utils.StringUtils.Exceptions.NO_DATA;


@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmRepository filmRepository;
    private final FilmMapper filmMapper;

    @Override
    @Transactional
    public void saveMedia(Long idFilm, MultipartFile file) {
        try {
            Film film = filmRepository.findById(idFilm)
                    .orElseThrow(() -> new NoSuchElementException("Film with ID " + idFilm + " not found"));
            if (file.isEmpty()) {
                throw new IllegalArgumentException("Uploaded file is empty");
            }
            film.setMedia(file.getBytes());
            filmRepository.save(film);
        } catch(Exception e) {
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
    @Override
    public byte[] getMediaFilm(Long id) {
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Film with ID " + id + " not found"));
        return film.getMedia();
    }
    @Override
    public String determineMediaType(byte[] media) {
        try {

            if (media.length >= 4) {
                String header = String.format("%02X%02X%02X%02X", media[0], media[1], media[2], media[3]);
                switch (header) {
                    case "FFD8FFDB": // JPEG
                    case "FFD8FFE0": // JPEG
                    case "FFD8FFE1": // JPEG
                        return "image/jpeg";
                    case "89504E47": // PNG
                        return "image/png";
                    default:
                        return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

