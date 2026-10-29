package com.nexus.patterns.state;

import com.nexus.model.PedidoReposicion;

public class Demorado implements EstadoPedido {

    public static final String NOMBRE = "DEMORADO";

    @Override
    public String nombre() {
        return NOMBRE;
    }

    @Override
    public EstadoPedido avanzar(PedidoReposicion pedido) {
        return Recibido.recibir(pedido);
    }
}
