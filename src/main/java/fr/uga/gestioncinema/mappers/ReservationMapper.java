package fr.uga.gestioncinema.mappers;

import fr.uga.gestioncinema.configurations.MapperConfig;
import fr.uga.gestioncinema.dto.ReservationDto;
import fr.uga.gestioncinema.entities.Reservation;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.openapitools.model.ReservationOpenApiModel;

import java.util.List;

/**
 * MapStruct mapper for Reservation entity and DTOs.
 */
@Mapper(config = MapperConfig.class, uses = {UserMapper.class, FilmProjectionMapper.class})
public interface ReservationMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "filmProjection.id", target = "filmProjectionId")
    ReservationDto toDto(Reservation entity);

    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "filmProjectionId", target = "filmProjection.id")
    @Mapping(target = "tickets", ignore = true)
    Reservation toEntity(ReservationDto dto);

    List<ReservationDto> toDtoList(List<Reservation> entities);

    // OpenAPI Model mappings
    ReservationDto toDto(ReservationOpenApiModel model);

    ReservationOpenApiModel toOpenApiModel(ReservationDto dto);

    List<ReservationOpenApiModel> toOpenApiModelList(List<ReservationDto> dtoList);

    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "filmProjectionId", target = "filmProjection.id")
    @Mapping(target = "tickets", ignore = true)
    void update(@Valid ReservationDto source, @MappingTarget Reservation target);
}
