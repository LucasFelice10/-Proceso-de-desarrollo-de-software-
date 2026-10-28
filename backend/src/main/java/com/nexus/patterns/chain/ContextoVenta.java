package com.nexus.patterns.chain;

import com.nexus.model.Producto;

/** Datos que viajan por la cadena durante el procesamiento de una venta. */
public class ContextoVenta {

    private final String sku;
    private final int cantidad;
    private Producto producto;
    private int stockAnterior;
    private boolean umbralCruzado;
    private boolean alertaDisparada;

    public ContextoVenta(String sku, int cantidad) {
        this.sku = sku;
        this.cantidad = cantidad;
    }

    public String getSku() {
        return sku;
    }

    public int getCantidad() {
        return cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getStockAnterior() {
        return stockAnterior;
    }

    public void setStockAnterior(int stockAnterior) {
        this.stockAnterior = stockAnterior;
    }

    public boolean isUmbralCruzado() {
        return umbralCruzado;
    }

    public void setUmbralCruzado(boolean umbralCruzado) {
        this.umbralCruzado = umbralCruzado;
    }

    public boolean isAlertaDisparada() {
        return alertaDisparada;
    }

    public void setAlertaDisparada(boolean alertaDisparada) {
        this.alertaDisparada = alertaDisparada;
    }
}
