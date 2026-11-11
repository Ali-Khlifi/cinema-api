package fr.uga.gestioncinema.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;

/**
 * Entity representing a Film in the cinema management system.
 */
@Entity
@Table(name = "film")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Film implements Serializable {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 255)
    private String title;
    
    private double duration;
    
    private String director;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "release_date")
    private LocalDate releaseDate;
    
    @Column(name = "media", columnDefinition = "bytea")
    private byte[] media;

    /** S3 object key of the poster. Null until a poster is uploaded. */
    @Column(name = "poster_key", length = 512)
    private String posterKey;

    /** TMDB movie id. Null for films that were not imported. */
    @Column(name = "tmdb_id", unique = true)
    private Integer tmdbId;

    /** YouTube video id of the trailer. Null when TMDB has no trailer. */
    @Column(name = "trailer_youtube_key", length = 32)
    private String trailerYoutubeKey;
    
    @OneToMany(mappedBy = "film")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonIgnore
    private Collection<FilmProjection> filmProjections;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnore
    private Category category;
}
