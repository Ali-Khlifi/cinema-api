package fr.uga.gestioncinema.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Collection;
import java.util.Date;
@Entity
@Data

public class FilmProjection {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date dateProjection;
    private double prix;
    @ManyToOne
    private Salle salle;
    @ManyToOne
    private Film film;
    @OneToMany(mappedBy = "filmProjection")
    private Collection<Ticket> tickets;
    ///@ManyToOne
    //private Seance seance;
    @OneToMany(mappedBy = "filmProjection")
    private Collection<Seance> seances;

}
