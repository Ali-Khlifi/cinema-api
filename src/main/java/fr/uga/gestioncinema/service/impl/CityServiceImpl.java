package fr.uga.gestioncinema.service.impl;

import fr.uga.gestioncinema.dto.CityDto;
import fr.uga.gestioncinema.exceptions.CustomErrorService;
import fr.uga.gestioncinema.mappers.CityMapper;
import fr.uga.gestioncinema.repositories.CityRepository;
import fr.uga.gestioncinema.service.CityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for City entity operations.
 */
@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {

    @Autowired
    CustomErrorService customErrorService;

    private final CityRepository repository;
    private final CityMapper mapper;

    @Override
    public CityDto fetchOne(Long id) {
        return mapper.toDto(repository.findById(id)
                .orElseThrow(() -> customErrorService.customErrorException("City not found", HttpStatus.NOT_FOUND)));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public CityDto save(CityDto dto) {
        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    @Override
    public CityDto update(@Valid final CityDto dto) {
        final var city = repository.findById(dto.getId())
                .orElseThrow(() -> customErrorService.customErrorException("City not found", HttpStatus.NOT_FOUND));

        mapper.update(dto, city);
        repository.save(city);

        return mapper.toDto(city);
    }

    @Override
    public List<CityDto> fetchAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}
