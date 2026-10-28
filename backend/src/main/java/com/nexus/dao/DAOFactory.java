package com.nexus.dao;

/**
 * Abstract Factory de persistencia: cada motor de datos entrega su familia completa de DAOs.
 * El negocio solo conoce esta clase y las interfaces, así que cambiar de motor no lo toca.
 */
public abstract class DAOFactory {

    public abstract IProductoDAO getProductoDAO();

    public abstract IProveedorDAO getProveedorDAO();

    public abstract IPedidoDAO getPedidoDAO();

    public abstract IMovimientoDAO getMovimientoDAO();
}
