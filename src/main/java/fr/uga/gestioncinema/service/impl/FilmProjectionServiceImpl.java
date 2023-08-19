package fr.uga.gestioncinema.service.impl;

import fr.uga.gestioncinema.dto.FilmProjectionDto;
import fr.uga.gestioncinema.exceptions.NotFoundException;
import fr.uga.gestioncinema.mappers.FilmProjectionMapper;
import fr.uga.gestioncinema.repositories.FilmProjectionRepository;
import fr.uga.gestioncinema.service.FilmProjectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static fr.uga.gestioncinema.utils.StringUtils.Exceptions.NO_DATA;


@Service
@RequiredArgsConstructor
public class FilmProjectionServiceImpl implements FilmProjectionService {
    private final FilmProjectionRepository repository;
    private final FilmProjectionMapper mapper;

    @Override
    public FilmProjectionDto fetchOne(Long id) {
        return mapper.toDto(repository.findById(id)
                .orElseThrow(() -> new NotFoundException(NO_DATA)));
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
    public List<FilmProjectionDto> findBySalle(Long salleId) {
        return repository.findBySalle_Id(salleId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public FilmProjectionDto update(FilmProjectionDto dto) {
        final var filmProjection = repository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException(NO_DATA));

        mapper.update(dto, filmProjection);

        return mapper.toDto(repository.save(filmProjection));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);

    }

}

