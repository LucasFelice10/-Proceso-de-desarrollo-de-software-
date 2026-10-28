package com.nexus.patterns.strategy;

import com.nexus.model.Proveedor;

import java.util.List;

public interface IEstrategiaProveedor {

    /** Elige entre los candidatos el proveedor para reponer el producto indicado. */
    Proveedor seleccionar(String sku, List<Proveedor> candidatos);
}
