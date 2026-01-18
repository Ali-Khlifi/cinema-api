package fr.uga.gestioncinema.dto;

import fr.uga.gestioncinema.entities.Reservation.ReservationStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ReservationDto {
    private Long id;
    private String referenceNumber;
    private Long userId;
    private Long filmProjectionId;
    private ReservationStatus status;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private String promoCode;
    private LocalDateTime reservationDate;
    private LocalDateTime expirationDate;
    private LocalDateTime confirmationDate;
    private LocalDateTime cancellationDate;
    private String cancellationReason;
    private Boolean termsAccepted;
    private Boolean marketingConsent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}



