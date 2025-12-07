package fr.uga.gestioncinema.service.impl;

import fr.uga.gestioncinema.dto.SeanceDto;
import fr.uga.gestioncinema.exceptions.CustomErrorService;
import fr.uga.gestioncinema.mappers.SeanceMapper;
import fr.uga.gestioncinema.repositories.SeanceRepository;
import fr.uga.gestioncinema.service.SeanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class SeanceServiceImpl implements SeanceService {

    @Autowired
    CustomErrorService customErrorService;
    private final SeanceRepository repository;
    private final SeanceMapper mapper;

    @Override
    public SeanceDto fetchOne(Long id) {
        return mapper.toDto(repository.findById(id)
                .orElseThrow(() -> customErrorService.customErrorException("Not found", HttpStatus.NOT_FOUND)));

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
    public List<SeanceDto> fetchByFilmProjection(Long filmProjectionId) {
        return repository.findByFilmProjection_Id(filmProjectionId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }
    @Override
    public SeanceDto update(SeanceDto dto) {
        final var seance = repository.findById(dto.getId())
                .orElseThrow(() -> customErrorService.customErrorException("Not found", HttpStatus.NOT_FOUND));

        mapper.update(dto, seance);

        return mapper.toDto(repository.save(seance));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);

    }

}

