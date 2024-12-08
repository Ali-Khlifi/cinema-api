package fr.uga.gestioncinema.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.Date;
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Film {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titre;
    private double duree;
    private String realisateur;
    private String description;
    private Date dateSortie;
    @Lob
    @Column(name = "media", nullable = true)
    private byte [] media;
    @OneToMany(mappedBy = "film")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // pour bloquer l'affichage des projection (en lecture)// lors de la consultation des films
    private Collection<FilmProjection> filmProjections;
    @ManyToOne // id_categorie est une clé étrangère dans films
    private Category category;
}
