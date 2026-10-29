package com.nexus.service;

import com.nexus.dao.IPedidoDAO;
import com.nexus.dao.IProveedorDAO;
import com.nexus.model.PedidoReposicion;
import com.nexus.model.Proveedor;
import com.nexus.patterns.strategy.EstrategiaBalanceada;
import com.nexus.patterns.strategy.IEstrategiaProveedor;

import java.util.Objects;

/**
 * Contexto del patrón Strategy: arma los pedidos de reposición delegando la elección del
 * proveedor en la estrategia activa. Cambiar de perfil no toca esta clase.
 */
public class GeneradorPedidos {

    private final IProveedorDAO proveedorDAO;
    private final IPedidoDAO pedidoDAO;
    private final ReposicionService reposicion;
    private volatile IEstrategiaProveedor estrategia = new EstrategiaBalanceada();

    public GeneradorPedidos(IProveedorDAO proveedorDAO, IPedidoDAO pedidoDAO, ReposicionService reposicion) {
        this.proveedorDAO = proveedorDAO;
        this.pedidoDAO = pedidoDAO;
        this.reposicion = reposicion;
    }

    public IEstrategiaProveedor getEstrategia() {
        return estrategia;
    }

    /** Cambia el perfil de selección de proveedor; por defecto es el balanceado. */
    public void setEstrategia(IEstrategiaProveedor estrategia) {
        this.estrategia = Objects.requireNonNull(estrategia, "La estrategia no puede ser nula");
    }

    /**
     * Pedido One-Click: calcula la cantidad sugerida, elige el proveedor con la estrategia activa y
     * guarda el pedido en estado GENERADO. El mensaje al proveedor y el paso a ENVIADO quedan del
     * lado del controlador y del State.
     *
     * @throws java.util.NoSuchElementException si el producto no existe o ningún proveedor lo ofrece
     * @throws IllegalStateException si el stock, sumado a lo ya pedido, supera el punto de pedido
     */
    public PedidoReposicion ejecutarConexionOneClick(String sku) {
        int cantidad = reposicion.cantidadSugerida(sku);
        if (cantidad == 0) {
            throw new IllegalStateException("No hace falta reponer " + sku
                    + ": el stock más lo ya pedido supera el punto de pedido");
        }
        Proveedor proveedor = estrategia.seleccionar(sku, proveedorDAO.listarPorProducto(sku));
        PedidoReposicion pedido = new PedidoReposicion(sku, proveedor.getId(), cantidad);
        pedidoDAO.guardar(pedido);
        return pedido;
    }
}
