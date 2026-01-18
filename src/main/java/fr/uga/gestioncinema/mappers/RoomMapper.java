package fr.uga.gestioncinema.mappers;

import fr.uga.gestioncinema.configurations.MapperConfig;
import fr.uga.gestioncinema.dto.RoomDto;
import fr.uga.gestioncinema.entities.Room;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.openapitools.model.RoomOpenApiModel;

import java.util.List;

/**
 * MapStruct mapper for Room entity and DTOs.
 */
@Mapper(config = MapperConfig.class)
public interface RoomMapper {

    @Mapping(source = "cinema.id", target = "cinemaId")
    RoomDto toDto(Room entity);

    @Mapping(source = "cinemaId", target = "cinema.id")
    @Mapping(target = "seats", ignore = true)
    @Mapping(target = "filmProjections", ignore = true)
    Room toEntity(RoomDto dto);

    RoomDto toDto(RoomOpenApiModel model);

    RoomOpenApiModel toOpenApiModel(RoomDto dto);

    List<RoomOpenApiModel> toOpenApiModelList(List<RoomDto> dtoList);

    @Mapping(source = "cinemaId", target = "cinema.id")
    @Mapping(target = "seats", ignore = true)
    @Mapping(target = "filmProjections", ignore = true)
    void update(@Valid RoomDto source, @MappingTarget Room target);
}
