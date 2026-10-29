package com.nexus.patterns.state;

import com.nexus.model.PedidoReposicion;

public class Enviado implements EstadoPedido {

    public static final String NOMBRE = "ENVIADO";

    @Override
    public String nombre() {
        return NOMBRE;
    }

    @Override
    public EstadoPedido avanzar(PedidoReposicion pedido) {
        throw new IllegalStateException("Transición desde ENVIADO pendiente de implementar");
    }
}
