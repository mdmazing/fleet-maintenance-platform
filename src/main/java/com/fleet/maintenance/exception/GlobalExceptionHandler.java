package com.fleet.maintenance.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex, WebRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(ConflictException ex, WebRequest request) {
        log.warn("Conflict: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request);
    }

    // Triggered by @Valid failures on @RequestBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        Map<String, Object> body = buildBody(HttpStatus.BAD_REQUEST, "Validation Failed", request);
        body.put("fieldErrors", fieldErrors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Triggered when an invalid enum value is passed in the request body (e.g. "INVALID" as AssetType)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadable(
            HttpMessageNotReadableException ex, WebRequest request) {
        log.warn("Unreadable request: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request",
                "Invalid value in request body. Check enum fields (AssetType, AssetStatus, etc.).", request);
    }

    // Triggered when an invalid enum value is passed as a @PathVariable (e.g. GET /assets/status/INVALID)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        String message = String.format("Invalid value '%s' for parameter '%s'", ex.getValue(), ex.getName());
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", message, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex, WebRequest request) {
        log.error("Unhandled exception", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "An unexpected error occurred.", request);
    }

    private ResponseEntity<Map<String, Object>> buildResponse(
            HttpStatus status, String error, String message, WebRequest request) {
        return new ResponseEntity<>(buildBody(status, error, request, message), status);
    }

    private Map<String, Object> buildBody(HttpStatus status, String error, WebRequest request) {
        return buildBody(status, error, request, null);
    }

    private Map<String, Object> buildBody(HttpStatus status, String error, WebRequest request, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        if (message != null) body.put("message", message);
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return body;
    }
}
