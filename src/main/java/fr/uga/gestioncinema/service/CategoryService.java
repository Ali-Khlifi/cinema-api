package fr.uga.gestioncinema.service;

import fr.uga.gestioncinema.dto.CategoryDto;
import fr.uga.gestioncinema.dto.FilmDto;

import javax.validation.Valid;
import java.util.List;

public interface CategoryService {
    CategoryDto save(@Valid CategoryDto category);
    CategoryDto update(@Valid CategoryDto category);
    List<CategoryDto> fetchAll();

    CategoryDto fetchOne(Long id);
    void delete(Long id);

}
