package com.hospital.admision.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Convierte las excepciones en respuestas JSON con un mensaje legible
 * para que el frontend pueda mostrarlo.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Rechazo de @PreAuthorize (rol sin permiso)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> manejarAccesoDenegado(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("mensaje", "No tiene permisos para realizar esta operación."));
    }

    // Reglas de negocio y registros no encontrados
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> manejarErrorNegocio(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("mensaje", ex.getMessage() != null ? ex.getMessage() : "Error inesperado."));
    }
}
