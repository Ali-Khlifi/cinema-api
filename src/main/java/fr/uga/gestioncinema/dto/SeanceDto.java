package fr.uga.gestioncinema.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SeanceDto {
    private Long id;
    private LocalDateTime heureDebut;
    private LocalDateTime heureFin;

}
