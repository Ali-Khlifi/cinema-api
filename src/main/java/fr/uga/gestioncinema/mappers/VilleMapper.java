package fr.uga.gestioncinema.mappers;

import fr.uga.gestioncinema.configurations.MapperConfig;
import fr.uga.gestioncinema.dto.VilleDto;
import fr.uga.gestioncinema.entities.Ville;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.openapitools.model.VilleOpenApiModel;

import java.util.List;

@Mapper(config = MapperConfig.class)
public interface VilleMapper {


    VilleDto toDto(Ville entity);
    VilleDto toDto(VilleOpenApiModel model);
    List<VilleOpenApiModel> toOpenApiModelList(List<VilleDto> dtoList);
    Ville toEntity(VilleDto dto);

    VilleOpenApiModel toOpenApiModel(VilleDto dto);

    void update(@Valid VilleDto source, @MappingTarget Ville target);

}
