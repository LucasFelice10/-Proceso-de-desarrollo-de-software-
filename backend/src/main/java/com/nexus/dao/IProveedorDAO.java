package com.nexus.dao;

import com.nexus.model.Proveedor;

import java.util.List;
import java.util.Optional;

public interface IProveedorDAO {

    Optional<Proveedor> buscarPorId(long id);

    List<Proveedor> listarTodos();

    /** Proveedores que ofrecen el producto, con su oferta cargada. */
    List<Proveedor> listarPorProducto(String sku);

    void guardar(Proveedor proveedor);
}
