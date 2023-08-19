package fr.uga.gestioncinema.exceptions;

import lombok.experimental.StandardException;

/**
 * Exception thrown when resource is not found
 * this is a runtime exception because its only needs to be treated by the controller api
 *
 * @author ali khlifi
 */

@StandardException
public class NotFoundException extends RuntimeException {
    private static final long serialVersionUID = 75693903200617L;
}
