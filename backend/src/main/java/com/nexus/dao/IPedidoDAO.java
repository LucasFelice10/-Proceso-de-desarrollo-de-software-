package com.nexus.dao;

import com.nexus.model.PedidoReposicion;

import java.util.List;
import java.util.Optional;

public interface IPedidoDAO {

    Optional<PedidoReposicion> buscarPorId(long id);

    List<PedidoReposicion> listarTodos();

    List<PedidoReposicion> listarPorProducto(String sku);

    /** Historial de entregas: pedidos del producto en estado RECIBIDO, con fecha de envío y de recepción. */
    List<PedidoReposicion> listarRecibidosPorProducto(String sku);

    /** Inserta el pedido y le asigna el id generado. */
    void guardar(PedidoReposicion pedido);

    void actualizar(PedidoReposicion pedido);
}
