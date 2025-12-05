package org.proj.lab5;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Помилки валідації @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return errors;
    }

    @ExceptionHandler(UserNotFoundException.class)
    public Map<String, String> handleNotFound(UserNotFoundException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(InvalidUserDataException.class)
    public Map<String, String> handleInvalid(InvalidUserDataException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public Map<String, String> handleEmail(EmailAlreadyExistsException e) {
        return Map.of("error", e.getMessage());
    }
}