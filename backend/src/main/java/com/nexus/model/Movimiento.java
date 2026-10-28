package com.nexus.model;

import java.time.LocalDateTime;

public class Movimiento {

    public enum Tipo { VENTA, RECEPCION }

    private long id;
    private String sku;
    private Tipo tipo;
    private int cantidad;
    private LocalDateTime fecha;

    public Movimiento() {
    }

    public Movimiento(String sku, Tipo tipo, int cantidad) {
        this.sku = sku;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = LocalDateTime.now();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
