package fr.uga.gestioncinema.service.impl;

import fr.uga.gestioncinema.dto.CinemaDto;
import fr.uga.gestioncinema.dto.VilleDto;
import fr.uga.gestioncinema.entities.Ville;
import fr.uga.gestioncinema.exceptions.NotFoundException;
import fr.uga.gestioncinema.mappers.CinemaMapper;
import fr.uga.gestioncinema.mappers.VilleMapper;
import fr.uga.gestioncinema.repositories.CinemaRepository;
import fr.uga.gestioncinema.service.CinemaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static fr.uga.gestioncinema.utils.StringUtils.Exceptions.NO_DATA;


@Service
@RequiredArgsConstructor
public class CinemaServiceImpl implements CinemaService {
    private final CinemaRepository repository;
    private final CinemaMapper mapper;
    private final VilleMapper villeMapper;

    @Override
    public CinemaDto fetchOne(Long id) {
        return mapper.toDto(repository.findById(id)
                .orElseThrow(() -> new NotFoundException(NO_DATA)));
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
                .orElseThrow(() -> new NotFoundException(NO_DATA));

        mapper.update(dto, cinema);

        return mapper.toDto(repository.save(cinema));
    }

    @Override
    public List<CinemaDto> findByVille(VilleDto dto) {

        return repository.findByVilleName(dto.getName())
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);

    }

}

