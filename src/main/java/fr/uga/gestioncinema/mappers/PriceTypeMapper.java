package fr.uga.gestioncinema.mappers;

import fr.uga.gestioncinema.configurations.MapperConfig;
import fr.uga.gestioncinema.dto.PriceTypeDto;
import fr.uga.gestioncinema.entities.PriceType;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.openapitools.model.PriceTypeOpenApiModel;

import java.util.List;

/**
 * MapStruct mapper for PriceType entity and DTOs.
 */
@Mapper(config = MapperConfig.class)
public interface PriceTypeMapper {

    PriceTypeDto toDto(PriceType entity);

    @Mapping(target = "tickets", ignore = true)
    PriceType toEntity(PriceTypeDto dto);

    List<PriceTypeDto> toDtoList(List<PriceType> entities);

    // OpenAPI Model mappings
    PriceTypeDto toDto(PriceTypeOpenApiModel model);

    PriceTypeOpenApiModel toOpenApiModel(PriceTypeDto dto);

    List<PriceTypeOpenApiModel> toOpenApiModelList(List<PriceTypeDto> dtoList);

    @Mapping(target = "tickets", ignore = true)
    void update(@Valid PriceTypeDto source, @MappingTarget PriceType target);
}
