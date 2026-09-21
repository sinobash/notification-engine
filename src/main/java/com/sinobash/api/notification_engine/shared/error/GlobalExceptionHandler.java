package com.sinobash.api.notification_engine.shared.error;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice 
public class GlobalExceptionHandler {
    
    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<?> notFound(NotFoundException e)
    {
        return ResponseEntity
                .status(404)
                .body(Map.of(
                    "timestamp", Instant.now(),
                    "error", e.getMessage()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class) 
    ResponseEntity<?> validation(MethodArgumentNotValidException e)
    {
        return ResponseEntity.badRequest()
                .body(Map.of(
                    "timestamp", Instant.now(),
                    "error", "Validation failed",
                    "details", e.getBindingResult().getFieldErrors().stream().map(x->x.getField()+": "+x.getDefaultMessage()).toList()
                ));
    }
}
