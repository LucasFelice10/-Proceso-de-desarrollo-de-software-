package com.nexus.patterns.observer;

import com.nexus.dao.IProductoDAO;
import com.nexus.model.Producto;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;

public class InventarioObservable {

    private final IProductoDAO productoDAO;
    private final List<Observador> observadores = new CopyOnWriteArrayList<>();

    public InventarioObservable(IProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }

    public void agregarObservador(Observador observador) {
        observadores.add(observador);
    }

    public void quitarObservador(Observador observador) {
        observadores.remove(observador);
    }

    /** Resta stock, lo persiste y avisa a los observadores. */
    public Producto descontarStock(String sku, int cantidad) {
        Producto producto = buscar(sku);
        producto.setStockActual(producto.getStockActual() - cantidad);
        productoDAO.actualizar(producto);
        notificar(producto);
        return producto;
    }

    /** Suma stock (recepción de un pedido), lo persiste y avisa a los observadores. */
    public Producto agregarStock(String sku, int cantidad) {
        Producto producto = buscar(sku);
        producto.setStockActual(producto.getStockActual() + cantidad);
        productoDAO.actualizar(producto);
        notificar(producto);
        return producto;
    }

    private Producto buscar(String sku) {
        return productoDAO.buscarPorSku(sku)
                .orElseThrow(() -> new NoSuchElementException("Producto inexistente: " + sku));
    }

    private void notificar(Producto producto) {
        for (Observador observador : observadores) {
            observador.actualizar(producto);
        }
    }
}
