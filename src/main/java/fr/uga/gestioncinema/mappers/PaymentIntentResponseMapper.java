package fr.uga.gestioncinema.mappers;

import fr.uga.gestioncinema.dto.PaymentIntentResponse;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for PaymentIntentResponse
 */
@Mapper(componentModel = "spring")
public interface PaymentIntentResponseMapper {

    /**
     * Convert internal DTO to OpenAPI model
     */
    org.openapitools.model.PaymentIntentResponse toOpenApiModel(PaymentIntentResponse dto);

    /**
     * Convert OpenAPI model to internal DTO
     */
    PaymentIntentResponse toDto(org.openapitools.model.PaymentIntentResponse model);
}









