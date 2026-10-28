package com.nexus.patterns.chain;

import com.nexus.dao.IProductoDAO;
import com.nexus.model.Producto;

import java.util.NoSuchElementException;

public class ValidarStockFisico extends ManejadorVenta {

    private final IProductoDAO productoDAO;

    public ValidarStockFisico(IProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }

    @Override
    public void procesar(ContextoVenta contexto) {
        if (contexto.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        Producto producto = productoDAO.buscarPorSku(contexto.getSku())
                .orElseThrow(() -> new NoSuchElementException("Producto inexistente: " + contexto.getSku()));
        if (producto.getStockActual() < contexto.getCantidad()) {
            throw new StockInsuficienteException("Stock insuficiente de " + producto.getNombre()
                    + ": hay " + producto.getStockActual() + " y se piden " + contexto.getCantidad());
        }
        contexto.setProducto(producto);
        contexto.setStockAnterior(producto.getStockActual());
        pasarAlSiguiente(contexto);
    }
}
