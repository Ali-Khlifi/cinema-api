package fr.uga.gestioncinema.mappers;

import fr.uga.gestioncinema.configurations.MapperConfig;
import fr.uga.gestioncinema.dto.SeatDto;
import fr.uga.gestioncinema.entities.Seat;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.openapitools.model.SeatOpenApiModel;

import java.util.List;

/**
 * MapStruct mapper for Seat entity and DTOs.
 */
@Mapper(config = MapperConfig.class)
public interface SeatMapper {

    @Mapping(source = "room.id", target = "roomId")
    SeatDto toDto(Seat entity);

    @Mapping(source = "roomId", target = "room.id")
    @Mapping(target = "tickets", ignore = true)
    Seat toEntity(SeatDto dto);

    SeatDto toDto(SeatOpenApiModel model);

    SeatOpenApiModel toOpenApiModel(SeatDto dto);

    List<SeatOpenApiModel> toOpenApiModelList(List<SeatDto> dtoList);

    @Mapping(source = "roomId", target = "room.id")
    @Mapping(target = "tickets", ignore = true)
    void update(@Valid SeatDto source, @MappingTarget Seat target);
}
