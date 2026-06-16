package com.paulstna.notification.exception;

import com.paulstna.notification.exception.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(
            ResourceNotFoundException exception, WebRequest webRequest) {
        return buildError(exception.getMessage(), HttpStatus.NOT_FOUND, webRequest);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceAlreadyExists(
            ResourceAlreadyExistsException exception, WebRequest webRequest) {
        return buildError(exception.getMessage(), HttpStatus.CONFLICT, webRequest);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalState(
            IllegalStateException exception, WebRequest webRequest) {
        return buildError(exception.getMessage(), HttpStatus.CONFLICT, webRequest);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException exception) {
        Map<String, String> validationErrors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            validationErrors.put(fieldName, error.getDefaultMessage());
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationErrors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGlobalException(
            Exception exception, WebRequest webRequest) {
        return buildError(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, webRequest);
    }

    private ResponseEntity<ErrorResponseDTO> buildError(
            String message, HttpStatus status, WebRequest webRequest) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .apiPath(webRequest.getDescription(false))
                .errorCode(status.value())
                .errorMessage(message)
                .errorTime(LocalDateTime.now())
                .build();
        return ResponseEntity.status(status).body(error);
    }
}
