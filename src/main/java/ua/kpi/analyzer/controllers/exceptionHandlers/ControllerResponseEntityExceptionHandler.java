package ua.kpi.analyzer.controllers.exceptionHandlers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ua.kpi.analyzer.exceptions.WrongFormatSpecialtyException;

@ControllerAdvice
public class ControllerResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { WrongFormatSpecialtyException.class })
    protected ResponseEntity<Object> wrongFormatSpecialty(WrongFormatSpecialtyException ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = { WebClientResponseException.Unauthorized.class })
    protected ResponseEntity<Object> unauthorized(WebClientResponseException.Unauthorized ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getMessage() + ". API key and/or insttoken might not be set.",
                new HttpHeaders(), ex.getStatusCode(), request);
    }
}