package fr.uga.gestioncinema.service.impl;

import fr.uga.gestioncinema.dto.FilmProjectionDto;
import fr.uga.gestioncinema.exceptions.CustomErrorService;
import fr.uga.gestioncinema.mappers.FilmProjectionMapper;
import fr.uga.gestioncinema.repositories.FilmProjectionRepository;
import fr.uga.gestioncinema.service.FilmProjectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class FilmProjectionServiceImpl implements FilmProjectionService {
    @Autowired
    CustomErrorService customErrorService;
    private final FilmProjectionRepository repository;
    private final FilmProjectionMapper mapper;

    @Override
    public FilmProjectionDto fetchOne(Long id) {
        return mapper.toDto(repository.findById(id)
                .orElseThrow(() -> customErrorService.customErrorException("Not found", HttpStatus.NOT_FOUND)));

    }

    @Override
    public FilmProjectionDto save(FilmProjectionDto dto) {
        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    @Override
    public List<FilmProjectionDto> fetchAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }
    @Override
    public List<FilmProjectionDto> findByRoom(Long roomId) {
        return repository.findUniqueProjectionsByRoomId(roomId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public FilmProjectionDto update(FilmProjectionDto dto) {
        final var filmProjection = repository.findById(dto.getId())
                .orElseThrow(() -> customErrorService.customErrorException("Not found", HttpStatus.NOT_FOUND));

        mapper.update(dto, filmProjection);

        return mapper.toDto(repository.save(filmProjection));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);

    }

}

