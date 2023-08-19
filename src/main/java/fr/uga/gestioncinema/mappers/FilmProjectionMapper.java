package fr.uga.gestioncinema.mappers;

import fr.uga.gestioncinema.configurations.MapperConfig;
import fr.uga.gestioncinema.dto.FilmProjectionDto;
import fr.uga.gestioncinema.entities.FilmProjection;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.openapitools.model.FilmProjectionOpenApiModel;

import java.util.List;

@Mapper(config = MapperConfig.class,  uses = {SeanceMapper.class, FilmMapper.class, SalleMapper.class})
public interface FilmProjectionMapper {

    @Mapping(source = "salle.id", target = "salleId")
    FilmProjectionDto toDto(FilmProjection entity);

    @Mapping(source = "salleId", target = "salle.id")
    FilmProjection toEntity(FilmProjectionDto dto);

    FilmProjectionDto toDto(FilmProjectionOpenApiModel model);

    FilmProjectionOpenApiModel toOpenApiModel(FilmProjectionDto dto);

    List<FilmProjectionOpenApiModel> toOpenApiModelList(List<FilmProjectionDto> listFilmProjectionDto);

    @Mapping(source = "salleId", target = "salle.id")
    void update(@Valid FilmProjectionDto source, @MappingTarget FilmProjection target);

}
