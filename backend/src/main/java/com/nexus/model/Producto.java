package com.nexus.model;

public class Producto {

    private String sku;
    private String nombre;
    private int stockActual;
    private int umbralCritico;

    public Producto() {
    }

    public Producto(String sku, String nombre, int stockActual, int umbralCritico) {
        this.sku = sku;
        this.nombre = nombre;
        this.stockActual = stockActual;
        this.umbralCritico = umbralCritico;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }

    public int getUmbralCritico() {
        return umbralCritico;
    }

    public void setUmbralCritico(int umbralCritico) {
        this.umbralCritico = umbralCritico;
    }
}
