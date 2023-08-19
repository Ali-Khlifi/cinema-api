package fr.uga.gestioncinema.mappers;

import fr.uga.gestioncinema.configurations.MapperConfig;
import fr.uga.gestioncinema.dto.SalleDto;
import fr.uga.gestioncinema.entities.Salle;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.openapitools.model.SalleOpenApiModel;

import java.util.List;

@Mapper(config = MapperConfig.class, uses = {PlaceMapper.class, CinemaMapper.class})
public interface SalleMapper {

    @Mapping(source = "cinema.id", target = "cinemaId")
    @Mapping(target = "filmProjections", ignore = true)
    SalleDto toDto(Salle salleEntity);

    @Mapping(source = "cinemaId", target = "cinema.id")
    @Mapping(target = "filmProjections", ignore = true)
    Salle toEntity(SalleDto dto);

    @Mapping(target = "filmProjections", ignore = true)
    SalleDto toDto(SalleOpenApiModel model);

    SalleOpenApiModel toOpenApiModel(SalleDto dto);

    List<SalleOpenApiModel> toOpenApiModelList(List<SalleDto> listSalleDto);

    @Mapping(source = "cinemaId", target = "cinema.id")
    void update(@Valid SalleDto source, @MappingTarget Salle target);

}
