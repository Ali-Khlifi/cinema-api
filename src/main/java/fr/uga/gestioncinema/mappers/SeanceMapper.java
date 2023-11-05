package fr.uga.gestioncinema.mappers;

import fr.uga.gestioncinema.configurations.MapperConfig;
import fr.uga.gestioncinema.dto.SeanceDto;
import fr.uga.gestioncinema.entities.Seance;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.openapitools.model.SeanceOpenApiModel;

import java.util.List;

@Mapper(config = MapperConfig.class, uses = {PlaceMapper.class, FilmProjectionMapper.class})
public interface SeanceMapper {

    @Mapping(source = "filmProjection.id", target = "filmProjectionId")
    SeanceDto toDto(Seance entity);
    SeanceDto toDto(SeanceOpenApiModel model);
    List<SeanceOpenApiModel> toOpenApiModelList(List<SeanceDto> dtoList);
    @Mapping(source = "filmProjectionId", target = "filmProjection.id")
    Seance toEntity(SeanceDto dto);

    SeanceOpenApiModel toOpenApiModel(SeanceDto dto);

    @Mapping(source = "filmProjectionId", target = "filmProjection.id")
    void update(@Valid SeanceDto source, @MappingTarget Seance target);

}
