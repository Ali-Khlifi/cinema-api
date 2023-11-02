package fr.uga.gestioncinema.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Builder
public class SeanceDto {
    private Long id;
    private LocalDateTime heureDebut;
    private LocalDateTime heureFin;

}
