package com.AuraHealth.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejo centralizado de errores para todos los controllers.
 * Convierte excepciones en respuestas JSON estructuradas.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Errores de @Valid (@NotBlank, @Email, @Size, @Pattern) → 400 con mapa campo:mensaje */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> campos = new HashMap<>();
        for (FieldError e : ex.getBindingResult().getFieldErrors())
            campos.put(e.getField(), e.getDefaultMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status",    HttpStatus.BAD_REQUEST.value());
        body.put("error",     "Error de validación en los datos del formulario");
        body.put("campos",    campos);

        return ResponseEntity.badRequest().body(body);
    }

    /** Errores de dominio lanzados con ResponseStatusException (404, 409, 401, 400) */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleDomainErrors(ResponseStatusException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status",    ex.getStatusCode().value());
        body.put("error",     ex.getReason());
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }

    /** 403 — sin permisos para el recurso (rol insuficiente) */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status",    HttpStatus.FORBIDDEN.value());
        body.put("error",     "Acceso denegado. No tienes permisos para este recurso.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    /** 401 — token ausente, inválido o expirado */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(AuthenticationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status",    HttpStatus.UNAUTHORIZED.value());
        body.put("error",     "Token inválido, expirado o ausente.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    /** Errores no controlados → 500 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status",    500);
        body.put("error",     "Error interno del servidor");
        return ResponseEntity.internalServerError().body(body);
    }
}