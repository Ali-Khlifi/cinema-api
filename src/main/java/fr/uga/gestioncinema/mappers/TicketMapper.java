package fr.uga.gestioncinema.mappers;

import fr.uga.gestioncinema.configurations.MapperConfig;
import fr.uga.gestioncinema.dto.TicketDto;
import fr.uga.gestioncinema.entities.Ticket;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.openapitools.model.TicketOpenApiModel;

import java.util.List;

@Mapper(config = MapperConfig.class, uses = {PlaceMapper.class, FilmProjectionMapper.class})
public interface TicketMapper {

    @Mapping(source = "place.id", target = "placeId")
    @Mapping(source = "filmProjection.id", target = "filmProjectionId")
    TicketDto toDto(Ticket entity);

    @Mapping(source = "placeId", target = "place.id")
    @Mapping(source = "filmProjectionId", target = "filmProjection.id")
    Ticket toEntity(TicketDto dto);

    TicketDto toDto(TicketOpenApiModel model);

    TicketOpenApiModel toOpenApiModel(TicketDto dto);

    List<TicketOpenApiModel> toOpenApiModelList(List<TicketDto> listTicketDto);

    @Mapping(source = "placeId", target = "place.id")
    @Mapping(source = "filmProjectionId", target = "filmProjection.id")
    void update(@Valid TicketDto source, @MappingTarget Ticket target);


}
