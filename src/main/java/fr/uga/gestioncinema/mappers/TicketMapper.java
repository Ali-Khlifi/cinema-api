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

/**
 * MapStruct mapper for Ticket entity and DTOs.
 */
@Mapper(config = MapperConfig.class, uses = {SeatMapper.class, ReservationMapper.class, PriceTypeMapper.class})
public interface TicketMapper {

    @Mapping(source = "reservation.id", target = "reservationId")
    @Mapping(source = "seat.id", target = "seatId")
    @Mapping(source = "priceType.id", target = "priceTypeId")
    TicketDto toDto(Ticket entity);

    @Mapping(source = "reservationId", target = "reservation.id")
    @Mapping(source = "seatId", target = "seat.id")
    @Mapping(source = "priceTypeId", target = "priceType.id")
    Ticket toEntity(TicketDto dto);

    TicketDto toDto(TicketOpenApiModel model);

    TicketOpenApiModel toOpenApiModel(TicketDto dto);

    List<TicketOpenApiModel> toOpenApiModelList(List<TicketDto> listTicketDto);

    @Mapping(source = "reservationId", target = "reservation.id")
    @Mapping(source = "seatId", target = "seat.id")
    @Mapping(source = "priceTypeId", target = "priceType.id")
    void update(@Valid TicketDto source, @MappingTarget Ticket target);
}
