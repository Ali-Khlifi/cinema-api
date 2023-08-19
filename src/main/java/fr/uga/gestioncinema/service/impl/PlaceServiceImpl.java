package fr.uga.gestioncinema.service.impl;

import fr.uga.gestioncinema.dto.PlaceDto;
import fr.uga.gestioncinema.dto.SalleDto;
import fr.uga.gestioncinema.exceptions.NotFoundException;
import fr.uga.gestioncinema.mappers.PlaceMapper;
import fr.uga.gestioncinema.mappers.SalleMapper;
import fr.uga.gestioncinema.repositories.PlaceRepository;
import fr.uga.gestioncinema.repositories.SalleRepository;
import fr.uga.gestioncinema.service.PlaceService;
import fr.uga.gestioncinema.service.SalleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static fr.uga.gestioncinema.utils.StringUtils.Exceptions.NO_DATA;


@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {
    private final PlaceRepository repository;
    private final PlaceMapper mapper;

    @Override
    public PlaceDto fetchOne(Long id) {
        return mapper.toDto(repository.findById(id)
                .orElseThrow(() -> new NotFoundException(NO_DATA)));
    }

    @Override
    public PlaceDto save(PlaceDto dto) {
        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    @Override
    public List<PlaceDto> fetchAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public PlaceDto update(PlaceDto dto) {
        final var place = repository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException(NO_DATA));

        mapper.update(dto, place);

        return mapper.toDto(repository.save(place));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);

    }

}

