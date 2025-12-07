package fr.uga.gestioncinema.exceptions;

import lombok.Getter;

public class CustomErrorException extends RuntimeException {
    @Getter
    private final transient CustomErrorResponse errorResponse;

    public CustomErrorException(String message, CustomErrorResponse errorResponse) {
        super(message);
        this.errorResponse = errorResponse;
    }
}
