package fr.uga.gestioncinema.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

@Getter
public enum ErrorCode {

    // FILMS
    FILM_NOT_FOUND("service-film-error-001", HttpStatus.NOT_FOUND),
    FILM_CREATION_ERROR("service-film-error-002", HttpStatus.BAD_REQUEST),
    FILM_ALREADY_EXISTS("service-film-error-003", HttpStatus.CONFLICT),
    FILM_INVALID_DURATION("service-film-error-004", HttpStatus.BAD_REQUEST),

    // CINEMAS
    CINEMA_NOT_FOUND("service-cinemas-error-001", HttpStatus.NOT_FOUND),

    // SALLES
    SALLE_NOT_FOUND("service-salle-error-001", HttpStatus.NOT_FOUND),
    SALLE_CAPACITY_EXCEEDED("service-salle-error-002", HttpStatus.CONFLICT),
    SALLE_ALREADY_BOOKED("service-salle-error-003", HttpStatus.CONFLICT),
    SALLE_EQUIPMENT_NOT_SUPPORTED("service-salle-error-004", HttpStatus.BAD_REQUEST),

    // SÉANCES
    SEANCE_NOT_FOUND("service-seance-error-001", HttpStatus.NOT_FOUND),
    SEANCE_SCHEDULE_CONFLICT("service-seance-error-002", HttpStatus.CONFLICT),
    SEANCE_INVALID_DATE("service-seance-error-003", HttpStatus.BAD_REQUEST),
    SEANCE_FULL("service-seance-error-004", HttpStatus.CONFLICT),

    // RÉSERVATIONS
    PLACE_UNAVAILABLE("service-reservation-error-001", HttpStatus.CONFLICT),
    RESERVATION_NOT_FOUND("service-reservation-error-002", HttpStatus.NOT_FOUND),
    RESERVATION_CANCELLATION_ERROR("service-reservation-error-003", HttpStatus.CONFLICT),
    INVALID_CLIENT_EMAIL("service-reservation-error-004", HttpStatus.BAD_REQUEST),
    RESERVATION_CANCELLATION_DEADLINE("service-reservation-error-005", HttpStatus.CONFLICT),

    // VALIDATION
    VALIDATION_ERROR("validation-error-001", HttpStatus.BAD_REQUEST),
    FIELD_REQUIRED("validation-error-002", HttpStatus.BAD_REQUEST),
    INVALID_FORMAT("validation-error-003", HttpStatus.BAD_REQUEST),
    VALUE_NOT_ALLOWED("validation-error-004", HttpStatus.BAD_REQUEST),

    // TECHNIQUE
    INTERNAL_ERROR("technical-error-001", HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_ERROR("technical-error-002", HttpStatus.SERVICE_UNAVAILABLE),
    EXTERNAL_SERVICE_ERROR("technical-error-003", HttpStatus.BAD_GATEWAY),
    DATA_ACCESS_ERROR("technical-error-004", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final HttpStatus httpStatus;

    ErrorCode(String code, HttpStatus httpStatus) {
        this.code = code;
        this.httpStatus = httpStatus;
    }

    // Method to get ErrorCode from code string
    public static ErrorCode fromCode(String code) {
        return Arrays.stream(values())
                .filter(errorCode -> errorCode.code.equals(code))
                .findFirst()
                .orElse(INTERNAL_ERROR);
    }
}
