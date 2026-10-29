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
    private volatile IEstrategiaProveedor estrategia = new EstrategiaBalanceada();

    public GeneradorPedidos(IProveedorDAO proveedorDAO, IPedidoDAO pedidoDAO) {
        this.proveedorDAO = proveedorDAO;
        this.pedidoDAO = pedidoDAO;
    }

    public IEstrategiaProveedor getEstrategia() {
        return estrategia;
    }

    /** Cambia el perfil de selección de proveedor; por defecto es el balanceado. */
    public void setEstrategia(IEstrategiaProveedor estrategia) {
        this.estrategia = Objects.requireNonNull(estrategia, "La estrategia no puede ser nula");
    }

    /**
     * Pedido One-Click: elige el proveedor con la estrategia activa y guarda el pedido en estado
     * GENERADO. El mensaje al proveedor y el paso a ENVIADO quedan del lado del controlador y del State.
     *
     * @throws IllegalArgumentException si la cantidad no es positiva
     * @throws java.util.NoSuchElementException si ningún proveedor ofrece el producto
     */
    public PedidoReposicion ejecutarConexionOneClick(String sku, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        Proveedor proveedor = estrategia.seleccionar(sku, proveedorDAO.listarPorProducto(sku));
        PedidoReposicion pedido = new PedidoReposicion(sku, proveedor.getId(), cantidad);
        pedidoDAO.guardar(pedido);
        return pedido;
    }
}
