package fr.uga.gestioncinema.mappers;

import fr.uga.gestioncinema.configurations.MapperConfig;
import fr.uga.gestioncinema.dto.FilmDto;
import fr.uga.gestioncinema.entities.Film;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.openapitools.model.FilmOpenApiModel;

import java.util.List;

/**
 * MapStruct mapper for Film entity and DTOs.
 */
@Mapper(config = MapperConfig.class, uses = {CategoryMapper.class})
public interface FilmMapper {

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(target = "filmProjections", ignore = true)
    FilmDto toDto(Film filmEntity);

    @Mapping(source = "categoryId", target = "category.id")
    @Mapping(target = "filmProjections", ignore = true)
    Film toEntity(FilmDto filmDto);

    @Mapping(target = "media", ignore = true) // Media is handled separately via upload/download endpoints
    @Mapping(target = "posterKey", ignore = true) // Set only by the S3 poster upload, not by the public film payload
    @Mapping(target = "filmProjections", ignore = true)
    FilmDto toDto(FilmOpenApiModel model);

    // MapStruct will only map fields that exist in both FilmDto and FilmOpenApiModel
    // Fields like 'media' and 'filmProjections' in FilmDto that don't exist in FilmOpenApiModel are automatically ignored
    FilmOpenApiModel toOpenApiModel(FilmDto filmDto);

    List<FilmOpenApiModel> toOpenApiModelList(List<FilmDto> listFilmDto);

    @Mapping(target = "filmProjections", ignore = true)
    void update(@Valid FilmDto source, @MappingTarget Film target);
}
