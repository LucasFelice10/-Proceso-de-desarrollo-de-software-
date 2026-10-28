package com.nexus.model;

import java.util.ArrayList;
import java.util.List;

public class Proveedor {

    private long id;
    private String nombre;
    private String contacto;
    private List<OfertaProveedor> ofertas = new ArrayList<>();

    public Proveedor() {
    }

    public Proveedor(long id, String nombre, String contacto) {
        this.id = id;
        this.nombre = nombre;
        this.contacto = contacto;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public List<OfertaProveedor> getOfertas() {
        return ofertas;
    }

    public void setOfertas(List<OfertaProveedor> ofertas) {
        this.ofertas = ofertas;
    }

    public OfertaProveedor getOferta(String sku) {
        return ofertas.stream()
                .filter(o -> o.getSku().equals(sku))
                .findFirst()
                .orElse(null);
    }
}
