package com.nexus.patterns.state;

import com.nexus.model.PedidoReposicion;

import java.util.NoSuchElementException;

public interface EstadoPedido {

    String nombre();

    EstadoPedido avanzar(PedidoReposicion pedido);

    /** Revisa si el pedido debe cambiar de estado por el paso del tiempo; por defecto no cambia. */
    default EstadoPedido evaluar(PedidoReposicion pedido) {
        return this;
    }

    /** Reconstruye el estado a partir del nombre guardado en el pedido. */
    static EstadoPedido de(String nombre) {
        return switch (nombre) {
            case Generado.NOMBRE -> new Generado();
            case Enviado.NOMBRE -> new Enviado();
            case Demorado.NOMBRE -> new Demorado();
            case Recibido.NOMBRE -> new Recibido();
            default -> throw new NoSuchElementException("Estado de pedido desconocido: " + nombre);
        };
    }
}
