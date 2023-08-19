package fr.uga.gestioncinema.service.impl;

import fr.uga.gestioncinema.dto.CinemaDto;
import fr.uga.gestioncinema.dto.SalleDto;
import fr.uga.gestioncinema.exceptions.NotFoundException;
import fr.uga.gestioncinema.mappers.CinemaMapper;
import fr.uga.gestioncinema.mappers.SalleMapper;
import fr.uga.gestioncinema.repositories.CinemaRepository;
import fr.uga.gestioncinema.repositories.SalleRepository;
import fr.uga.gestioncinema.service.CinemaService;
import fr.uga.gestioncinema.service.SalleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static fr.uga.gestioncinema.utils.StringUtils.Exceptions.NO_DATA;


@Service
@RequiredArgsConstructor
public class SalleServiceImpl implements SalleService {
    private final SalleRepository repository;
    private final SalleMapper mapper;

    @Override
    public SalleDto fetchOne(Long id) {
        return mapper.toDto(repository.findById(id)
                .orElseThrow(() -> new NotFoundException(NO_DATA)));
    }

    @Override
    public SalleDto save(SalleDto dto) {
        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    @Override
    public List<SalleDto> fetchAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public SalleDto update(SalleDto dto) {
        final var salle = repository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException(NO_DATA));

        mapper.update(dto, salle);

        return mapper.toDto(repository.save(salle));
    }

    @Override
    public List<SalleDto> findByCinema(CinemaDto dto) {
        return repository.findByCinemaName(dto.getName())
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);

    }

}

