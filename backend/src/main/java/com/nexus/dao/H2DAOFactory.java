package com.nexus.dao;

import com.nexus.dao.jdbc.MovimientoDAOJdbc;
import com.nexus.dao.jdbc.PedidoDAOJdbc;
import com.nexus.dao.jdbc.ProductoDAOJdbc;
import com.nexus.dao.jdbc.ProveedorDAOJdbc;

import javax.sql.DataSource;

/** Familia de DAOs JDBC sobre H2. Todos comparten el mismo DataSource. */
public class H2DAOFactory extends DAOFactory {

    private final IProductoDAO productoDAO;
    private final IProveedorDAO proveedorDAO;
    private final IPedidoDAO pedidoDAO;
    private final IMovimientoDAO movimientoDAO;

    public H2DAOFactory(DataSource dataSource) {
        this.productoDAO = new ProductoDAOJdbc(dataSource);
        this.proveedorDAO = new ProveedorDAOJdbc(dataSource);
        this.pedidoDAO = new PedidoDAOJdbc(dataSource);
        this.movimientoDAO = new MovimientoDAOJdbc(dataSource);
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
