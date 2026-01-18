package fr.uga.gestioncinema.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Collection;

/**
 * Data Transfer Object for Film entity.
 */
@Data
@Builder
public class FilmDto {
    private Long id;
    private String title;
    private Double duration;
    private String director;
    private String description;
    private byte[] media;
    private LocalDate releaseDate;
    private Long categoryId;
    private Collection<FilmProjectionDto> filmProjections;
}
