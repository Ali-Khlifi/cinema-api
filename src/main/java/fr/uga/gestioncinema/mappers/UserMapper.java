package fr.uga.gestioncinema.mappers;

import fr.uga.gestioncinema.configurations.MapperConfig;
import fr.uga.gestioncinema.dto.UserDto;
import fr.uga.gestioncinema.entities.User;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.openapitools.model.UserOpenApiModel;

import java.util.List;

/**
 * MapStruct mapper for User entity and DTOs.
 */
@Mapper(config = MapperConfig.class)
public interface UserMapper {

    UserDto toDto(User entity);

    @Mapping(target = "reservations", ignore = true)
    User toEntity(UserDto dto);

    List<UserDto> toDtoList(List<User> entities);

    // OpenAPI Model mappings
    UserDto toDto(UserOpenApiModel model);

    UserOpenApiModel toOpenApiModel(UserDto dto);

    List<UserOpenApiModel> toOpenApiModelList(List<UserDto> dtoList);

    @Mapping(target = "reservations", ignore = true)
    void update(@Valid UserDto source, @MappingTarget User target);
}
