package com.nexus.controller;

import com.nexus.patterns.strategy.EstrategiaBalanceada;
import com.nexus.patterns.strategy.EstrategiaPrecio;
import com.nexus.patterns.strategy.EstrategiaVelocidad;
import com.nexus.patterns.strategy.IEstrategiaProveedor;
import com.nexus.service.GeneradorPedidos;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api/estrategia")
public class EstrategiaController {

    public record SolicitudEstrategia(String perfil) {
    }

    private static final Map<String, Supplier<IEstrategiaProveedor>> PERFILES = Map.of(
            "PRECIO", EstrategiaPrecio::new,
            "VELOCIDAD", EstrategiaVelocidad::new,
            "BALANCEADA", EstrategiaBalanceada::new);

    private final GeneradorPedidos generador;

    public EstrategiaController(GeneradorPedidos generador) {
        this.generador = generador;
    }

    @PutMapping
    public Map<String, String> cambiar(@RequestBody SolicitudEstrategia solicitud) {
        String perfil = solicitud.perfil() == null ? "" : solicitud.perfil().trim().toUpperCase();
        Supplier<IEstrategiaProveedor> estrategia = PERFILES.get(perfil);
        if (estrategia == null) {
            throw new IllegalArgumentException("Perfil inválido: debe ser PRECIO, VELOCIDAD o BALANCEADA");
        }
        generador.setEstrategia(estrategia.get());
        return Map.of("perfil", perfil);
    }
}
