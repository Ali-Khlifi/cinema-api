package fr.uga.gestioncinema.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collection;

/**
 * Entity representing a Cinema in the management system.
 * A cinema has a detailed address and contains multiple screening rooms.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "cinema")
public class Cinema implements Serializable {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 255)
    private String name;
    
    // Detailed address fields
    @Column(length = 100)
    private String city;
    
    @Column(name = "postal_code", length = 10)
    private String postalCode;
    
    @Column(name = "street_number", length = 20)
    private String streetNumber;
    
    @Column(name = "street_name", length = 255)
    private String streetName;
    
    @Column(name = "number_of_rooms")
    private int numberOfRooms;
    
    // Bidirectional association - mappedBy "cinema" because OneToMany is on Cinema side
    @OneToMany(mappedBy = "cinema", cascade = CascadeType.REMOVE)
    private Collection<Room> rooms;
    
    @ManyToOne
    @JoinColumn(name = "city_id")
    private City cityEntity;
}
