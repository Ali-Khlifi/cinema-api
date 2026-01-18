package fr.uga.gestioncinema.service.impl;

import fr.uga.gestioncinema.dto.CinemaDto;
import fr.uga.gestioncinema.dto.CityDto;
import fr.uga.gestioncinema.exceptions.CustomErrorService;
import fr.uga.gestioncinema.exceptions.ErrorCode;
import fr.uga.gestioncinema.mappers.CinemaMapper;
import fr.uga.gestioncinema.mappers.CityMapper;
import fr.uga.gestioncinema.repositories.CinemaRepository;
import fr.uga.gestioncinema.service.CinemaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for Cinema entity operations.
 */
@Service
@RequiredArgsConstructor
public class CinemaServiceImpl implements CinemaService {
    
    @Autowired
    CustomErrorService customErrorService;
    
    private final CinemaRepository repository;
    private final CinemaMapper mapper;
    private final CityMapper cityMapper;

    @Override
    public CinemaDto fetchOne(Long id) {
        return mapper.toDto(repository.findById(id)
                .orElseThrow(() -> customErrorService.customErrorException(ErrorCode.CINEMA_NOT_FOUND, id)));
    }

    @Override
    public CinemaDto save(CinemaDto dto) {
        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    @Override
    public List<CinemaDto> fetchAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public CinemaDto update(CinemaDto dto) {
        final var cinema = repository.findById(dto.getId())
                .orElseThrow(() -> customErrorService.customErrorException("Cinema not found", HttpStatus.NOT_FOUND));

        mapper.update(dto, cinema);

        return mapper.toDto(repository.save(cinema));
    }

    @Override
    public List<CinemaDto> findByCity(CityDto dto) {
        return repository.findByCityEntityName(dto.getName())
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
