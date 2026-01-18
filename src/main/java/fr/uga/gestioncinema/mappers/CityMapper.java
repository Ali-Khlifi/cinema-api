package fr.uga.gestioncinema.mappers;

import fr.uga.gestioncinema.configurations.MapperConfig;
import fr.uga.gestioncinema.dto.CityDto;
import fr.uga.gestioncinema.entities.City;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.openapitools.model.CityOpenApiModel;

import java.util.List;

/**
 * MapStruct mapper for City entity and DTOs.
 */
@Mapper(config = MapperConfig.class)
public interface CityMapper {

    CityDto toDto(City entity);
    
    CityDto toDto(CityOpenApiModel model);
    
    List<CityOpenApiModel> toOpenApiModelList(List<CityDto> dtoList);
    
    @Mapping(target = "cinemas", ignore = true)
    City toEntity(CityDto dto);

    CityOpenApiModel toOpenApiModel(CityDto dto);

    @Mapping(target = "cinemas", ignore = true)
    void update(@Valid CityDto source, @MappingTarget City target);
}
