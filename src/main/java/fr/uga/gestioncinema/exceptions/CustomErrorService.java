package fr.uga.gestioncinema.exceptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class CustomErrorService {

    @Autowired
    private TraceIdHandlerService traceIdHandlerService;

    public CustomErrorResponse.CustomErrorResponseBuilder initError() {
        return CustomErrorResponse
                .builder()
                .traceId(traceIdHandlerService.getTraceId())
                .spanId(traceIdHandlerService.getSpanId())
                .timestamp(OffsetDateTime.now());
    }

    public CustomErrorDetails customErrorDetails(String errorMessage) {
        return new CustomErrorDetails(null, errorMessage);
    }

    public CustomErrorResponse customErrorResponse(HttpStatus status) {
        return initError().status(status).build();
    }

    public CustomErrorResponse customErrorResponse(HttpStatus status, ErrorDetails details) {
        return initError().status(status).error(details).build();
    }

    public CustomErrorResponse customErrorResponse(HttpStatus status, String message, ErrorDetails details) {
        return initError().status(status).message(message).error(details).build();
    }

    public CustomErrorException customErrorException(String msg, HttpStatus status) {
        return new CustomErrorException(msg, customErrorResponse(status, msg, customErrorDetails(msg)));
    }


}