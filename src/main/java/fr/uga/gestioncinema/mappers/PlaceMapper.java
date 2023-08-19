package fr.uga.gestioncinema.mappers;

import fr.uga.gestioncinema.configurations.MapperConfig;
import fr.uga.gestioncinema.dto.PlaceDto;
import fr.uga.gestioncinema.entities.Place;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.openapitools.model.PlaceOpenApiModel;

import java.util.List;

@Mapper(config = MapperConfig.class)
public interface PlaceMapper {

    @Mapping(source = "salle.id", target = "salleId")
    PlaceDto toDto(Place placeEntity);
    @Mapping(source = "salleId", target = "salle.id")
    Place toEntity(PlaceDto dto);

    PlaceDto toDto(PlaceOpenApiModel model);

    PlaceOpenApiModel toOpenApiModel(PlaceDto dto);

    List<PlaceOpenApiModel> toOpenApiModelList(List<PlaceDto> listPlaceDto);

    @Mapping(source = "salleId", target = "salle.id")
    void update(@Valid PlaceDto source, @MappingTarget Place target);

}
