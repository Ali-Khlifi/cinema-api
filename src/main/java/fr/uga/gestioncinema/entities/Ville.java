package fr.uga.gestioncinema.entities;

import fr.uga.gestioncinema.entities.Cinema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
@Entity
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Ville {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double longitude, latitude, altitude;
    // association bidirectionnelle coté ville
    @OneToMany(mappedBy = "ville")
    private Collection<Cinema> cinemas;

}
