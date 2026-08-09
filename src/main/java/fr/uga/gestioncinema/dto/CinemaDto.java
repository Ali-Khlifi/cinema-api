package fr.uga.gestioncinema.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object for Cinema entity.
 * Contains detailed address information.
 */
@Data
@Builder
public class CinemaDto {
    private Long id;
    private String name;
    private int numberOfRooms;
    private boolean hasCoffee;
    private Long cityId;
    
    // Detailed address fields
    private String city;
    private String postalCode;
    private String streetNumber;
    private String streetName;
}
