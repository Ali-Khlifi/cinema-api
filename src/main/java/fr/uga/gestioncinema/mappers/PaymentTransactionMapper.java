package fr.uga.gestioncinema.mappers;

import fr.uga.gestioncinema.dto.PaymentTransactionDto;
import fr.uga.gestioncinema.entities.PaymentTransaction;
import org.mapstruct.*;
import org.openapitools.model.PaymentTransactionOpenApiModel;

import java.util.List;

/**
 * MapStruct mapper for PaymentTransaction entity and DTO
 */
@Mapper(componentModel = "spring")
public interface PaymentTransactionMapper {

    /**
     * Convert entity to DTO
     */
    @Mapping(source = "reservation.id", target = "reservationId")
    PaymentTransactionDto toDto(PaymentTransaction entity);

    /**
     * Convert DTO to entity
     */
    @Mapping(source = "reservationId", target = "reservation.id")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PaymentTransaction toEntity(PaymentTransactionDto dto);

    /**
     * Convert list of entities to DTOs
     */
    List<PaymentTransactionDto> toDtoList(List<PaymentTransaction> entities);

    /**
     * Update existing entity from DTO
     */
    @Mapping(source = "reservationId", target = "reservation.id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(PaymentTransactionDto dto, @MappingTarget PaymentTransaction entity);

    // ========== OpenAPI Model Mappings ==========

    /**
     * Convert DTO to OpenAPI model
     */
    PaymentTransactionOpenApiModel toOpenApiModel(PaymentTransactionDto dto);

    /**
     * Convert list of DTOs to OpenAPI models
     */
    List<PaymentTransactionOpenApiModel> toOpenApiModelList(List<PaymentTransactionDto> dtos);

    /**
     * Convert OpenAPI model to DTO
     */
    PaymentTransactionDto toDto(PaymentTransactionOpenApiModel model);
}

