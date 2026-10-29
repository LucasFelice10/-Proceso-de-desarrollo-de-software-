package com.nexus.controller;

import com.nexus.patterns.observer.ObservadorSSE;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequestMapping("/api/eventos")
public class EventosController {

    private final ObservadorSSE observadorSSE;

    public EventosController(ObservadorSSE observadorSSE) {
        this.observadorSSE = observadorSSE;
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter suscribir() {
        SseEmitter emisor = observadorSSE.suscribir();
        try {
            // El comentario fuerza el envío de los headers para que el cliente quede conectado de inmediato.
            emisor.send(SseEmitter.event().comment("conectado"));
        } catch (IOException e) {
            emisor.completeWithError(e);
        }
        return emisor;
    }
}
