package com.nexus.patterns.state;

import com.nexus.model.PedidoReposicion;

import java.time.LocalDateTime;

public class Recibido implements EstadoPedido {

    public static final String NOMBRE = "RECIBIDO";

    static Recibido recibir(PedidoReposicion pedido) {
        Recibido recibido = new Recibido();
        pedido.setFechaRecepcion(LocalDateTime.now());
        pedido.setEstado(recibido.nombre());
        return recibido;
    }

    @Override
    public String nombre() {
        return NOMBRE;
    }

    @Override
    public EstadoPedido avanzar(PedidoReposicion pedido) {
        throw new IllegalStateException("El pedido " + pedido.getId() + " ya fue recibido");
    }
}
