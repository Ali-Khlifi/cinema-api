package fr.uga.gestioncinema.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collection;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "cinema")

public class Cinema implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double longitude, latitude, altitude;
    @Column(nullable=true)
    private String adresse;
    private int nombreSalles;
    // association biderectionnel ---> mappedBy
    // manppedBy "cinema" ici car OneToMany est de coté Cinema
    @OneToMany(mappedBy = "cinema", cascade = CascadeType.REMOVE )
    private Collection<Salle> salles;
    @ManyToOne
    private Ville ville;



}
