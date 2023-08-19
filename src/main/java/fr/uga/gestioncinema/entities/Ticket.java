package fr.uga.gestioncinema.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.uga.gestioncinema.entities.FilmProjection;
import fr.uga.gestioncinema.entities.Place;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Ticket {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 75)
    private String nomClient;
    private double prix;
    @Column(unique = false)
    private int codePayement;
    private boolean reservee;
    @ManyToOne
    private Place place;
    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private FilmProjection filmProjection;
}
