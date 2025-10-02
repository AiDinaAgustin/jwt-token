package com.example.jwt_token.exception;

import com.example.jwt_token.response.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Validation error
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>("Validation error", HttpStatus.BAD_REQUEST, errors));
    }

    // Constraint error (duplicate key, foreign key, dll.)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = "Data integrity violation: " + ex.getMostSpecificCause().getMessage();

        // Bisa bikin lebih spesifik kalau ada "duplicate key"
        if (message.contains("duplicate key")) {
            message = "Duplicate data error: data dengan nilai unik sudah ada.";
        }

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(message, HttpStatus.CONFLICT, null));
    }

    // Illegal argument
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(ex.getMessage(), HttpStatus.BAD_REQUEST, null));
    }

    // General error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("Internal server error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null));
    }
}
