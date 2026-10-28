package com.nexus.model;

/**
 * Relación Producto N - N Proveedor: cada proveedor ofrece un producto
 * con su propio precio y lead time.
 */
public class OfertaProveedor {

    private String sku;
    private double precio;
    private int leadTimeDias;

    public OfertaProveedor() {
    }

    public OfertaProveedor(String sku, double precio, int leadTimeDias) {
        this.sku = sku;
        this.precio = precio;
        this.leadTimeDias = leadTimeDias;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getLeadTimeDias() {
        return leadTimeDias;
    }

    public void setLeadTimeDias(int leadTimeDias) {
        this.leadTimeDias = leadTimeDias;
    }
}
