package fr.uga.gestioncinema.service.impl;

import fr.uga.gestioncinema.dto.PlaceDto;
import fr.uga.gestioncinema.exceptions.CustomErrorService;
import fr.uga.gestioncinema.mappers.PlaceMapper;
import fr.uga.gestioncinema.repositories.PlaceRepository;
import fr.uga.gestioncinema.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {
    @Autowired
    CustomErrorService customErrorService;
    private final PlaceRepository repository;
    private final PlaceMapper mapper;

    @Override
    public PlaceDto fetchOne(Long id) {
        return mapper.toDto(repository.findById(id)
                .orElseThrow(() -> customErrorService.customErrorException("Not found", HttpStatus.NOT_FOUND)));
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
                .orElseThrow(() -> customErrorService.customErrorException("Not found", HttpStatus.NOT_FOUND));


        mapper.update(dto, place);

        return mapper.toDto(repository.save(place));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);

    }

}

