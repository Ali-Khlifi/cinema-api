package fr.uga.gestioncinema.mappers;

import fr.uga.gestioncinema.configurations.MapperConfig;
import fr.uga.gestioncinema.dto.FilmProjectionDto;
import fr.uga.gestioncinema.entities.FilmProjection;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.openapitools.model.FilmProjectionOpenApiModel;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * MapStruct mapper for FilmProjection entity and DTOs.
 * Seance has been merged into FilmProjection.
 */
@Mapper(config = MapperConfig.class, uses = {FilmMapper.class, RoomMapper.class})
public interface FilmProjectionMapper {

    @Mapping(source = "room.id", target = "roomId")
    @Mapping(source = "film.id", target = "filmId")
    @Mapping(source = "film", target = "film")
    FilmProjectionDto toDto(FilmProjection entity);

    @Mapping(source = "roomId", target = "room.id")
    @Mapping(source = "filmId", target = "film.id")
    @Mapping(target = "reservations", ignore = true)
    FilmProjection toEntity(FilmProjectionDto dto);

    FilmProjectionDto toDto(FilmProjectionOpenApiModel model);

    FilmProjectionOpenApiModel toOpenApiModel(FilmProjectionDto dto);

    List<FilmProjectionOpenApiModel> toOpenApiModelList(List<FilmProjectionDto> listFilmProjectionDto);

    @Mapping(source = "roomId", target = "room.id")
    @Mapping(target = "reservations", ignore = true)
    void update(@Valid FilmProjectionDto source, @MappingTarget FilmProjection target);
    
    // Default conversion methods for LocalTime <-> LocalDateTime
    default LocalTime toLocalTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalTime() : null;
    }
    
    default LocalDateTime toLocalDateTime(LocalTime time) {
        return time != null ? LocalDateTime.of(java.time.LocalDate.now(), time) : null;
    }
    
    // Conversion methods for String <-> LocalTime
    default LocalTime stringToLocalTime(String timeString) {
        return timeString != null ? LocalTime.parse(timeString) : null;
    }
    
    default String localTimeToString(LocalTime time) {
        return time != null ? time.toString() : null;
    }
}
