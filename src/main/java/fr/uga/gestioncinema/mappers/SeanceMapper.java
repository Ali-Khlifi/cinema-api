package fr.uga.gestioncinema.mappers;

import fr.uga.gestioncinema.configurations.MapperConfig;
import fr.uga.gestioncinema.dto.SeanceDto;
import fr.uga.gestioncinema.entities.Seance;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.openapitools.model.SeanceOpenApiModel;

import java.util.List;

@Mapper(config = MapperConfig.class)
public interface SeanceMapper {

    SeanceDto toDto(Seance entity);
    SeanceDto toDto(SeanceOpenApiModel model);
    List<SeanceOpenApiModel> toOpenApiModelList(List<SeanceDto> dtoList);
    Seance toEntity(SeanceDto dto);

    SeanceOpenApiModel toOpenApiModel(SeanceDto dto);

    void update(@Valid SeanceDto source, @MappingTarget Seance target);

}
