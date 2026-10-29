package com.nexus.patterns.state;

import com.nexus.model.PedidoReposicion;

import java.time.Duration;
import java.time.LocalDateTime;

public class Enviado implements EstadoPedido {

    public static final String NOMBRE = "ENVIADO";

    /** Días sin recibir el pedido a partir de los cuales se considera demorado. */
    public static final int DIAS_DEMORA = 2;

    @Override
    public String nombre() {
        return NOMBRE;
    }

    @Override
    public EstadoPedido avanzar(PedidoReposicion pedido) {
        return Recibido.recibir(pedido);
    }

    @Override
    public EstadoPedido evaluar(PedidoReposicion pedido) {
        LocalDateTime envio = pedido.getFechaEnvio();
        if (envio == null || Duration.between(envio, LocalDateTime.now()).compareTo(Duration.ofDays(DIAS_DEMORA)) <= 0) {
            return this;
        }
        EstadoPedido demorado = new Demorado();
        pedido.setEstado(demorado.nombre());
        return demorado;
    }
}
