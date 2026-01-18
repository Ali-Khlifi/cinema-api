package fr.uga.gestioncinema.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Data Transfer Object for FilmProjection entity.
 * Includes time fields merged from Seance.
 * Price is managed at Ticket level with PriceType discounts.
 */
@Data
@Builder
public class FilmProjectionDto {
    private Long id;
    private LocalDate projectionDate;
    
    // Time fields merged from Seance
    private LocalTime startTime;
    private LocalTime endTime;
    
    // Maximum projections per day
    private Integer maxProjections;
    
    private Long filmId;
    private Long roomId;
    private FilmDto film;
}
