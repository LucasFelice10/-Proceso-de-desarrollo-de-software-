package com.nexus.patterns.strategy;

import com.nexus.model.Proveedor;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

/** Perfil Velocidad: elige al proveedor con menor lead time. Si dos empatan, gana el más barato. */
public class EstrategiaVelocidad implements IEstrategiaProveedor {

    @Override
    public Proveedor seleccionar(String sku, List<Proveedor> candidatos) {
        return candidatos.stream()
                .filter(p -> p.getOferta(sku) != null)
                .min(Comparator.comparingInt((Proveedor p) -> p.getOferta(sku).getLeadTimeDias())
                        .thenComparingDouble(p -> p.getOferta(sku).getPrecio()))
                .orElseThrow(() -> new NoSuchElementException("Ningún proveedor ofrece " + sku));
    }
}
