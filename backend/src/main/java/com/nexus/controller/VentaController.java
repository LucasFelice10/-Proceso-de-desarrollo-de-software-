package com.nexus.controller;

import com.nexus.model.Producto;
import com.nexus.patterns.chain.ContextoVenta;
import com.nexus.service.VentaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    public record SolicitudVenta(String sku, int cantidad) {
    }

    public record RespuestaVenta(String sku, int cantidad, int stockActual, boolean umbralCruzado,
                                 boolean alertaDisparada) {
    }

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RespuestaVenta registrar(@RequestBody SolicitudVenta solicitud) {
        if (solicitud.sku() == null || solicitud.sku().isBlank()) {
            throw new IllegalArgumentException("El sku es obligatorio");
        }
        try {
            ContextoVenta contexto = ventaService.registrarVenta(solicitud.sku(), solicitud.cantidad());
            Producto producto = contexto.getProducto();
            return new RespuestaVenta(contexto.getSku(), contexto.getCantidad(), producto.getStockActual(),
                    contexto.isUmbralCruzado(), contexto.isAlertaDisparada());
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }
}
