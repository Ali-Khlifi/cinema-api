package fr.uga.gestioncinema.service.impl;

import fr.uga.gestioncinema.dto.CategoryDto;
import fr.uga.gestioncinema.dto.VilleDto;
import fr.uga.gestioncinema.exceptions.NotFoundException;
import fr.uga.gestioncinema.mappers.CategoryMapper;
import fr.uga.gestioncinema.mappers.VilleMapper;
import fr.uga.gestioncinema.repositories.CategorieRepository;
import fr.uga.gestioncinema.repositories.VilleRepository;
import fr.uga.gestioncinema.service.CategoryService;
import fr.uga.gestioncinema.service.VilleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static fr.uga.gestioncinema.utils.StringUtils.Exceptions.NO_DATA;

@Service
@RequiredArgsConstructor
public class VilleServiceImp implements VilleService {

    private final VilleRepository repository;
    private final VilleMapper mapper;
    @Override
    public VilleDto fetchOne(Long id) {
        return mapper.toDto(repository.findById(id)
                .orElseThrow(() -> new NotFoundException(NO_DATA)));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public VilleDto save(VilleDto dto) {
       return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    @Override
    public VilleDto update(@Valid final VilleDto dto) {
        final var ville = repository.findById(dto.getId())
                .orElseThrow(()-> new NotFoundException(NO_DATA));

        mapper.update(dto, ville);
        repository.save(ville);

        return mapper.toDto(ville);
    }

    @Override
    public List<VilleDto> fetchAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}
