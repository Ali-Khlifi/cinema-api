package fr.uga.gestioncinema.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.uga.gestioncinema.entities.Cinema;
import fr.uga.gestioncinema.entities.FilmProjection;
import fr.uga.gestioncinema.entities.Place;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Salle {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int nombrePlaces;

    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Cinema cinema;
    @OneToMany(mappedBy = "salle", cascade = CascadeType.REMOVE)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Collection<Place> places;
    @OneToMany(mappedBy = "salle")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Collection<FilmProjection> filmProjections;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNombrePlaces() {
        return nombrePlaces;
    }

    public void setNombrePlaces(int nombrePlaces) {
        this.nombrePlaces = nombrePlaces;
    }

    public Cinema getCinema() {
        return cinema;
    }

    public void setCinema(Cinema cinema) {
        this.cinema = cinema;
    }

    public Collection<Place> getPlaces() {
        return places;
    }

    public void setPlaces(Collection<Place> places) {
        this.places = places;
    }

    public Collection<FilmProjection> getFilmProjections() {
        return filmProjections;
    }

    public void setFilmProjections(Collection<FilmProjection> filmProjections) {
        this.filmProjections = filmProjections;
    }
}
