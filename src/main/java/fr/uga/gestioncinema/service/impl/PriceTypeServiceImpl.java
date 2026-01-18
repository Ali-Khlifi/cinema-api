package fr.uga.gestioncinema.service.impl;

import fr.uga.gestioncinema.dto.PriceTypeDto;
import fr.uga.gestioncinema.exceptions.CustomErrorService;
import fr.uga.gestioncinema.mappers.PriceTypeMapper;
import fr.uga.gestioncinema.repositories.PriceTypeRepository;
import fr.uga.gestioncinema.service.PriceTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for PriceType entity operations.
 */
@Service
@RequiredArgsConstructor
public class PriceTypeServiceImpl implements PriceTypeService {

    @Autowired
    CustomErrorService customErrorService;

    private final PriceTypeRepository repository;
    private final PriceTypeMapper mapper;

    @Override
    public PriceTypeDto fetchOne(Long id) {
        return mapper.toDto(repository.findById(id)
                .orElseThrow(() -> customErrorService.customErrorException("Price type not found", HttpStatus.NOT_FOUND)));
    }

    @Override
    public PriceTypeDto findByCode(String code) {
        return mapper.toDto(repository.findByCode(code)
                .orElseThrow(() -> customErrorService.customErrorException("Price type not found with code: " + code, HttpStatus.NOT_FOUND)));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public PriceTypeDto save(PriceTypeDto dto) {
        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    @Override
    public PriceTypeDto update(@Valid final PriceTypeDto dto) {
        final var priceType = repository.findById(dto.getId())
                .orElseThrow(() -> customErrorService.customErrorException("Price type not found", HttpStatus.NOT_FOUND));

        mapper.update(dto, priceType);
        repository.save(priceType);

        return mapper.toDto(priceType);
    }

    @Override
    public List<PriceTypeDto> fetchAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<PriceTypeDto> fetchAllActive() {
        return repository.findByIsActiveTrue()
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}









