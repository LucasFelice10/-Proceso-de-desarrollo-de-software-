package com.nexus.patterns.state;

import com.nexus.model.PedidoReposicion;

import java.time.LocalDateTime;

public class Generado implements EstadoPedido {

    public static final String NOMBRE = "GENERADO";

    @Override
    public String nombre() {
        return NOMBRE;
    }

    @Override
    public EstadoPedido avanzar(PedidoReposicion pedido) {
        EstadoPedido siguiente = new Enviado();
        pedido.setFechaEnvio(LocalDateTime.now());
        pedido.setEstado(siguiente.nombre());
        return siguiente;
    }
}
