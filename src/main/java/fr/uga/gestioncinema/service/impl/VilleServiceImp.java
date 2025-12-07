package fr.uga.gestioncinema.service.impl;

import fr.uga.gestioncinema.dto.VilleDto;
import fr.uga.gestioncinema.exceptions.CustomErrorService;
import fr.uga.gestioncinema.mappers.VilleMapper;
import fr.uga.gestioncinema.repositories.VilleRepository;
import fr.uga.gestioncinema.service.VilleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VilleServiceImp implements VilleService {

    @Autowired
    CustomErrorService customErrorService;

    private final VilleRepository repository;
    private final VilleMapper mapper;
    @Override
    public VilleDto fetchOne(Long id) {
        return mapper.toDto(repository.findById(id)
                .orElseThrow(() -> customErrorService.customErrorException("Not found", HttpStatus.NOT_FOUND)));

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
                .orElseThrow(() -> customErrorService.customErrorException("Not found", HttpStatus.NOT_FOUND));


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
