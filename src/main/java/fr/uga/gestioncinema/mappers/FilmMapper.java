package fr.uga.gestioncinema.mappers;

import fr.uga.gestioncinema.configurations.MapperConfig;
import fr.uga.gestioncinema.dto.FilmDto;
import fr.uga.gestioncinema.entities.Film;
import fr.uga.gestioncinema.service.PosterUrlService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.openapitools.model.FilmOpenApiModel;

import java.util.List;

/**
 * MapStruct mapper for Film entity and DTOs.
 */
@Mapper(config = MapperConfig.class, uses = {CategoryMapper.class})
public abstract class FilmMapper {

    private PosterUrlService posterUrlService;

    @Autowired
    public void setPosterUrlService(PosterUrlService posterUrlService) {
        this.posterUrlService = posterUrlService;
    }

    @Named("presignPoster")
    public String presignPoster(String posterKey) {
        return posterUrlService.presign(posterKey);
    }

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(target = "filmProjections", ignore = true)
    public abstract FilmDto toDto(Film filmEntity);

    @Mapping(source = "categoryId", target = "category.id")
    @Mapping(target = "filmProjections", ignore = true)
    public abstract Film toEntity(FilmDto filmDto);

    @Mapping(target = "media", ignore = true) // Media is handled separately via upload/download endpoints
    @Mapping(target = "posterKey", ignore = true) // Set only by the S3 poster upload, not by the public film payload
    @Mapping(target = "tmdbId", ignore = true)
    @Mapping(target = "trailerYoutubeKey", ignore = true)
    @Mapping(target = "filmProjections", ignore = true)
    @BeanMapping(ignoreUnmappedSourceProperties = "posterUrl")
    public abstract FilmDto toDto(FilmOpenApiModel model);

    // MapStruct will only map fields that exist in both FilmDto and FilmOpenApiModel
    // Fields like 'media' and 'filmProjections' in FilmDto that don't exist in FilmOpenApiModel are automatically ignored
    @Mapping(target = "posterUrl", source = "posterKey", qualifiedByName = "presignPoster")
    public abstract FilmOpenApiModel toOpenApiModel(FilmDto filmDto);

    public abstract List<FilmOpenApiModel> toOpenApiModelList(List<FilmDto> listFilmDto);

    @Mapping(target = "filmProjections", ignore = true)
    public abstract void update(@Valid FilmDto source, @MappingTarget Film target);
}
