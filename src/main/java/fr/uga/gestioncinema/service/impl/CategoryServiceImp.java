package fr.uga.gestioncinema.service.impl;

import fr.uga.gestioncinema.repositories.CategorieRepository;
import fr.uga.gestioncinema.dto.CategoryDto;
import fr.uga.gestioncinema.exceptions.NotFoundException;
import fr.uga.gestioncinema.mappers.CategoryMapper;
import fr.uga.gestioncinema.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static fr.uga.gestioncinema.utils.StringUtils.Exceptions.NO_DATA;

@Service
@RequiredArgsConstructor
public class CategoryServiceImp implements CategoryService {

    private final CategorieRepository repository;
    private final CategoryMapper mapper;
    @Override
    public CategoryDto fetchOne(Long id) {
        return mapper.toDto(repository.findById(id)
                .orElseThrow(() -> new NotFoundException(NO_DATA)));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
       return mapper.toDto(repository.save(mapper.toEntity(categoryDto)));
    }

    @Override
    public CategoryDto update(@Valid final CategoryDto categoryDto) {
        final var category = repository.findById(categoryDto.getId())
                .orElseThrow(()-> new NotFoundException(NO_DATA));

        mapper.update(categoryDto, category);
        repository.save(category);

        return mapper.toDto(category);
    }

    @Override
    public List<CategoryDto> fetchAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}
