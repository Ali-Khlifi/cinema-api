package fr.uga.gestioncinema.mappers;

import fr.uga.gestioncinema.configurations.MapperConfig;
import fr.uga.gestioncinema.dto.CinemaDto;
import fr.uga.gestioncinema.entities.Cinema;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.openapitools.model.CinemaOpenApiModel;

import java.util.List;

/**
 * MapStruct mapper for Cinema entity and DTOs.
 */
@Mapper(config = MapperConfig.class)
public interface CinemaMapper {

    @Mapping(source = "cityEntity.id", target = "cityId")
    CinemaDto toDto(Cinema cinemaEntity);

    @Mapping(source = "cityId", target = "cityEntity.id")
    @Mapping(target = "rooms", ignore = true)
    Cinema toEntity(CinemaDto cinemaDto);

    CinemaDto toDto(CinemaOpenApiModel model);

    CinemaOpenApiModel toOpenApiModel(CinemaDto cinemaDto);

    List<CinemaOpenApiModel> toOpenApiModelList(List<CinemaDto> listCinemaDto);

    @Mapping(source = "cityId", target = "cityEntity.id")
    @Mapping(target = "rooms", ignore = true)
    void update(@Valid CinemaDto source, @MappingTarget Cinema target);
}
