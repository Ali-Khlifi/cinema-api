package fr.uga.gestioncinema.mappers;

import fr.uga.gestioncinema.configurations.MapperConfig;
import fr.uga.gestioncinema.dto.CategoryDto;
import fr.uga.gestioncinema.dto.FilmDto;
import fr.uga.gestioncinema.entities.Category;
import fr.uga.gestioncinema.entities.Film;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.openapitools.model.CategoryOpenApiModel;
import org.openapitools.model.FilmOpenApiModel;

import java.util.List;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {

    CategoryDto toDto(Category entity);
    CategoryDto toDto(CategoryOpenApiModel model);
    List<CategoryOpenApiModel> toOpenApiModelList(List<CategoryDto> dtoList);
    Category toEntity(CategoryDto dto);

    CategoryOpenApiModel toOpenApiModel(CategoryDto dto);

    void update(@Valid CategoryDto source, @MappingTarget Category target);

}
