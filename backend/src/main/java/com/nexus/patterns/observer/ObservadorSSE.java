package com.nexus.patterns.observer;

import com.nexus.model.Producto;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class ObservadorSSE implements Observador {

    private final List<SseEmitter> emisores = new CopyOnWriteArrayList<>();

    /** Abre un stream para un cliente nuevo; lo usa el endpoint /api/eventos. */
    public SseEmitter suscribir() {
        SseEmitter emisor = new SseEmitter(0L);
        emisor.onCompletion(() -> emisores.remove(emisor));
        emisor.onTimeout(() -> emisores.remove(emisor));
        emisor.onError(e -> emisores.remove(emisor));
        emisores.add(emisor);
        return emisor;
    }

    @Override
    public void actualizar(Producto producto) {
        emitir("stock", datos(producto));
        if (producto.getStockActual() <= producto.getUmbralCritico()) {
            emitir("alerta", datos(producto));
        }
    }

    private Map<String, Object> datos(Producto producto) {
        return Map.of(
                "sku", producto.getSku(),
                "nombre", producto.getNombre(),
                "stockActual", producto.getStockActual(),
                "umbralCritico", producto.getUmbralCritico());
    }

    private void emitir(String evento, Object datos) {
        for (SseEmitter emisor : emisores) {
            try {
                emisor.send(SseEmitter.event().name(evento).data(datos));
            } catch (IOException | IllegalStateException e) {
                emisores.remove(emisor);
            }
        }
    }
}
