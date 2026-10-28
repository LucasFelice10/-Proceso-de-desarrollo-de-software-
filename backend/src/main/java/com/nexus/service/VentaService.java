package com.nexus.service;

import com.nexus.dao.IProductoDAO;
import com.nexus.patterns.chain.ContextoVenta;
import com.nexus.patterns.chain.DescontarStock;
import com.nexus.patterns.chain.DispararAlerta;
import com.nexus.patterns.chain.ManejadorVenta;
import com.nexus.patterns.chain.ValidarStockFisico;
import com.nexus.patterns.chain.VerificarUmbral;
import com.nexus.patterns.observer.InventarioObservable;

public class VentaService {

    private final ManejadorVenta cadena;

    public VentaService(IProductoDAO productoDAO, InventarioObservable inventario) {
        ManejadorVenta primero = new ValidarStockFisico(productoDAO);
        primero.setSiguiente(new DescontarStock(inventario))
                .setSiguiente(new VerificarUmbral())
                .setSiguiente(new DispararAlerta());
        this.cadena = primero;
    }

    /**
     * Registra una venta pasándola por la cadena de validación.
     *
     * @throws com.nexus.patterns.chain.StockInsuficienteException si no alcanza el stock
     * @throws IllegalArgumentException si la cantidad no es positiva
     * @throws java.util.NoSuchElementException si el producto no existe
     */
    public ContextoVenta registrarVenta(String sku, int cantidad) {
        ContextoVenta contexto = new ContextoVenta(sku, cantidad);
        cadena.procesar(contexto);
        return contexto;
    }
}
