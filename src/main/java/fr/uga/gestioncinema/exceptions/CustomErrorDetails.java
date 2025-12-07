package fr.uga.gestioncinema.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomErrorDetails implements ErrorDetails{

    private String errorCode;
    private String errorMessage;
}
