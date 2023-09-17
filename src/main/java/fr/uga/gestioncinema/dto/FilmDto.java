package fr.uga.gestioncinema.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Builder(toBuilder = true)
public class FilmDto {
    private Long id;
    private String titre;
    private Double duree;
    private String realisateur;
    private String description;
    private String photo;
    private LocalDateTime dateSortie;
    private Long categoryId;
    private Collection<FilmProjectionDto> filmProjections;

}
