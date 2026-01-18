package fr.uga.gestioncinema.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;

/**
 * Entity representing a Film Projection (screening schedule).
 * Merged with Seance entity - now includes start_time and end_time.
 * Price is managed at the Ticket level with PriceType discounts.
 */
@Entity
@Table(name = "film_projection")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FilmProjection implements Serializable {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "projection_date", nullable = false)
    private LocalDate projectionDate;
    
    // Time fields merged from Seance
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
    
    // Maximum projections per day for this film (0 = unlimited)
    @Column(name = "max_projections")
    private Integer maxProjections = 0;
    
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
    
    @ManyToOne
    @JoinColumn(name = "film_id", nullable = false)
    private Film film;
    
    @OneToMany(mappedBy = "filmProjection")
    private Collection<Reservation> reservations;
}
