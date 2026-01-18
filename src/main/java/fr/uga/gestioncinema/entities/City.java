package fr.uga.gestioncinema.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collection;

/**
 * Entity representing a City in the cinema management system.
 * A city can contain multiple cinemas.
 */
@Entity
@Table(name = "city")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class City implements Serializable {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 255)
    private String name;
    
    // Bidirectional association with Cinema
    @OneToMany(mappedBy = "city")
    private Collection<Cinema> cinemas;
}
