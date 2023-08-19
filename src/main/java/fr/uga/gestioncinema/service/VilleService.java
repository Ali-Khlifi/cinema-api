package fr.uga.gestioncinema.service;

import fr.uga.gestioncinema.dto.CategoryDto;
import fr.uga.gestioncinema.dto.VilleDto;

import javax.validation.Valid;
import java.util.List;

public interface VilleService {
    VilleDto save(@Valid VilleDto dto);
    VilleDto update(@Valid VilleDto dto);
    List<VilleDto> fetchAll();

    VilleDto fetchOne(Long id);
    void delete(Long id);

}
