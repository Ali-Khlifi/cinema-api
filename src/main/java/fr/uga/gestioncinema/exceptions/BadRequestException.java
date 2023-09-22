package fr.uga.gestioncinema.exceptions;

import lombok.experimental.StandardException;

/**
 * Exception thrown when a request is not formed correctly
 * this is a runtimeException because it only needs to be threated by the controller
 *
 * @author ali khlifi
 */

@StandardException
public class BadRequestException extends RuntimeException{
    private static final long serialVersionUID =8945687891087L;

}
