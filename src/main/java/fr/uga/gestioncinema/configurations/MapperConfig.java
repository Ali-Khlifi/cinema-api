package fr.uga.gestioncinema.configurations;


import org.mapstruct.InjectionStrategy;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;
import org.mapstruct.Mapper;

/**
 * Must be used as base configuration in all {@link Mapper}
 *
 *  @author ali khlifi
 */

@org.mapstruct.MapperConfig(componentModel = SPRING, nullValuePropertyMappingStrategy = IGNORE, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MapperConfig {
}
