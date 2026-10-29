package com.nexus.controller;

import com.nexus.patterns.chain.StockInsuficienteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.NoSuchElementException;

/** Traduce las excepciones del negocio a códigos HTTP con el cuerpo {"error": "..."}. */
@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler({IllegalArgumentException.class, StockInsuficienteException.class})
    public ResponseEntity<Map<String, String>> solicitudInvalida(RuntimeException e) {
        return responder(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> noEncontrado(NoSuchElementException e) {
        return responder(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> conflicto(IllegalStateException e) {
        return responder(HttpStatus.CONFLICT, e);
    }

    private ResponseEntity<Map<String, String>> responder(HttpStatus estado, RuntimeException e) {
        return ResponseEntity.status(estado).body(Map.of("error", String.valueOf(e.getMessage())));
    }
}
