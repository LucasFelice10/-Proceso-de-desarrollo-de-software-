package com.nexus.patterns.strategy;

import com.nexus.model.Proveedor;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

/** Perfil Precio: elige al proveedor más barato. Si dos empatan, gana el que entrega antes. */
public class EstrategiaPrecio implements IEstrategiaProveedor {

    @Override
    public Proveedor seleccionar(String sku, List<Proveedor> candidatos) {
        return candidatos.stream()
                .filter(p -> p.getOferta(sku) != null)
                .min(Comparator.comparingDouble((Proveedor p) -> p.getOferta(sku).getPrecio())
                        .thenComparingInt(p -> p.getOferta(sku).getLeadTimeDias()))
                .orElseThrow(() -> new NoSuchElementException("Ningún proveedor ofrece " + sku));
    }
}
