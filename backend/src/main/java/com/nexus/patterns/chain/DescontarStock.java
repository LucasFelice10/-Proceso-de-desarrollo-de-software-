package com.nexus.patterns.chain;

import com.nexus.patterns.observer.InventarioObservable;

public class DescontarStock extends ManejadorVenta {

    private final InventarioObservable inventario;

    public DescontarStock(InventarioObservable inventario) {
        this.inventario = inventario;
    }

    @Override
    public void procesar(ContextoVenta contexto) {
        contexto.setProducto(inventario.descontarStock(contexto.getSku(), contexto.getCantidad()));
        pasarAlSiguiente(contexto);
    }
}
