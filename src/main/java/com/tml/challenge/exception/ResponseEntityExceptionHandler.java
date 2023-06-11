package com.tml.challenge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class ResponseEntityExceptionHandler {

    /**
     * Handles {@link MethodArgumentTypeMismatchException} and returns a {@link ResponseEntity} with a {@link HttpStatus#BAD_REQUEST} status code
     * and a message indicating the invalid parameter.
     *
     * @param ex the exception to be handled
     * @return a {@link ResponseEntity} with a {@link HttpStatus#BAD_REQUEST} status code
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String parameterName = ex.getName();
        String errorMessage = String.format("Bad request: Invalid value for parameter '%s'.", parameterName);
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

}
