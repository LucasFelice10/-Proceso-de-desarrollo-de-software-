package com.nexus.patterns.state;

import com.nexus.model.PedidoReposicion;

public interface EstadoPedido {

    String nombre();

    EstadoPedido avanzar(PedidoReposicion pedido);
}
