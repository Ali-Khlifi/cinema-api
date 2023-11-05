package fr.uga.gestioncinema.service.impl;

import fr.uga.gestioncinema.dto.CinemaDto;
import fr.uga.gestioncinema.dto.SeanceDto;
import fr.uga.gestioncinema.exceptions.NotFoundException;
import fr.uga.gestioncinema.mappers.CinemaMapper;
import fr.uga.gestioncinema.mappers.SeanceMapper;
import fr.uga.gestioncinema.repositories.CinemaRepository;
import fr.uga.gestioncinema.repositories.SeanceRepository;
import fr.uga.gestioncinema.service.CinemaService;
import fr.uga.gestioncinema.service.SeanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static fr.uga.gestioncinema.utils.StringUtils.Exceptions.NO_DATA;


@Service
@RequiredArgsConstructor
public class SeanceServiceImpl implements SeanceService {
    private final SeanceRepository repository;
    private final SeanceMapper mapper;

    @Override
    public SeanceDto fetchOne(Long id) {
        return mapper.toDto(repository.findById(id)
                .orElseThrow(() -> new NotFoundException(NO_DATA)));
    }

    @Override
    public SeanceDto save(SeanceDto dto) {
        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    @Override
    public List<SeanceDto> fetchAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public SeanceDto update(SeanceDto dto) {
        final var seance = repository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException(NO_DATA));

        mapper.update(dto, seance);

        return mapper.toDto(repository.save(seance));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);

    }
    @Override
    public List<SeanceDto> findByFilmProjection(Long filmProjectionId) {
        return repository.findByFilmProjection_Id(filmProjectionId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}

