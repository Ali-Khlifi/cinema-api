package fr.uga.gestioncinema.exceptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    CustomErrorService customErrorService;

    @ExceptionHandler({ CustomErrorException.class })
    protected ResponseEntity<CustomErrorResponse> handleCustomError(RuntimeException ex) {
        CustomErrorException customErrorException = (CustomErrorException) ex;
        return ResponseEntity.status(customErrorException.getErrorResponse().getStatus())
                .body(customErrorException.getErrorResponse());
    }

    @Override
    @ResponseStatus
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status,
            WebRequest request) {

        CustomErrorDetails customErrorDetails = customErrorService.customErrorDetails(ex.getMessage());

        CustomErrorResponse customErrorResponse = customErrorService.customErrorResponse(HttpStatus.BAD_REQUEST, customErrorDetails);

        return ResponseEntity.status(customErrorResponse.getStatus())
                .body(customErrorResponse);
    }
}
