package fr.uga.gestioncinema.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PriceTypeDto {
    private Long id;
    private String code;
    private String label;
    private String description;
    private BigDecimal discountPercentage;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

