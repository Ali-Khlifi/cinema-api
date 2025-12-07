package fr.uga.gestioncinema.exceptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class CustomErrorService {

    @Autowired
    private TraceIdHandlerService traceIdHandlerService;

    @Autowired
    private MessageSource messageSource;

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

    public CustomErrorDetails customErrorDetails(ErrorCode errorCode, Object... args) {
        String message = messageSource.getMessage(errorCode.getCode(), args, LocaleContextHolder.getLocale());
        return new CustomErrorDetails(errorCode.getCode(), message);
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

    public CustomErrorException customErrorException(ErrorCode errorCode, Object... args) {
        CustomErrorDetails errorDetails = customErrorDetails(errorCode, args);
        return new CustomErrorException(errorDetails.getErrorMessage(), customErrorResponse(errorCode.getHttpStatus(), errorDetails.getErrorMessage(), errorDetails));
    }


}