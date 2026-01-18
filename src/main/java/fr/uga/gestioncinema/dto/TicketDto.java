package fr.uga.gestioncinema.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Ticket entity.
 */
@Data
@Builder
public class TicketDto {
    private Long id;
    private Long reservationId;
    private Long seatId;
    private Long priceTypeId;
    private BigDecimal basePrice;
    private BigDecimal finalPrice;
    private String qrCode;
    private LocalDateTime qrCodeGeneratedAt;
    private Boolean isUsed;
    private LocalDateTime usedAt;
    private String validatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
