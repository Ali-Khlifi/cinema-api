package fr.uga.gestioncinema.exceptions;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.uga.gestioncinema.configurations.ErrorDetailsSerializer;

@JsonSerialize(using = ErrorDetailsSerializer.class)
public interface ErrorDetails {

    String getErrorCode();

    String getErrorMessage();
}
