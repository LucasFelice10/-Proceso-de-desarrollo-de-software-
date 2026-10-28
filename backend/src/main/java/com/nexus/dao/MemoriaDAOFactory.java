package com.nexus.dao;

import com.nexus.dao.memoria.DatosDemo;
import com.nexus.dao.memoria.MovimientoDAOMemoria;
import com.nexus.dao.memoria.PedidoDAOMemoria;
import com.nexus.dao.memoria.ProductoDAOMemoria;
import com.nexus.dao.memoria.ProveedorDAOMemoria;

/**
 * Familia de DAOs sobre colecciones en memoria, sin base de datos.
 * Existe para demostrar que el negocio funciona igual al cambiar de motor.
 */
public class MemoriaDAOFactory extends DAOFactory {

    private final IProductoDAO productoDAO = new ProductoDAOMemoria();
    private final IProveedorDAO proveedorDAO = new ProveedorDAOMemoria();
    private final IPedidoDAO pedidoDAO = new PedidoDAOMemoria();
    private final IMovimientoDAO movimientoDAO = new MovimientoDAOMemoria();

    /** Fábrica vacía. */
    public MemoriaDAOFactory() {
    }

    /** Fábrica cargada con los mismos datos que data.sql. */
    public static MemoriaDAOFactory conDatosDemo() {
        MemoriaDAOFactory fabrica = new MemoriaDAOFactory();
        DatosDemo.cargar(fabrica);
        return fabrica;
    }

    @Override
    public IProductoDAO getProductoDAO() {
        return productoDAO;
    }

    @Override
    public IProveedorDAO getProveedorDAO() {
        return proveedorDAO;
    }

    @Override
    public IPedidoDAO getPedidoDAO() {
        return pedidoDAO;
    }

    @Override
    public IMovimientoDAO getMovimientoDAO() {
        return movimientoDAO;
    }
}
