package fr.uga.gestioncinema.service.impl;

import fr.uga.gestioncinema.dto.CategoryDto;
import fr.uga.gestioncinema.exceptions.CustomErrorService;
import fr.uga.gestioncinema.mappers.CategoryMapper;
import fr.uga.gestioncinema.repositories.CategorieRepository;
import fr.uga.gestioncinema.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImp implements CategoryService {

    private final CategorieRepository repository;
    private final CategoryMapper mapper;
    @Autowired
    CustomErrorService customErrorService;
    @Override
    public CategoryDto fetchOne(Long id) {
        return mapper.toDto(repository.findById(id)
                .orElseThrow(() -> customErrorService.customErrorException("Not found", HttpStatus.NOT_FOUND)));
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
                .orElseThrow(() -> customErrorService.customErrorException("Not found", HttpStatus.NOT_FOUND));


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
    @Override
    public CategoryDto findByName(String name) {
        return mapper.toDto(repository.findByName(name)
                .orElseThrow(() -> customErrorService.customErrorException("Not found", HttpStatus.NOT_FOUND)));

    }
}
