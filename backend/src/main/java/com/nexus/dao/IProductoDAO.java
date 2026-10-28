package com.nexus.dao;

import com.nexus.model.Producto;

import java.util.List;
import java.util.Optional;

public interface IProductoDAO {

    Optional<Producto> buscarPorSku(String sku);

    List<Producto> listarTodos();

    void guardar(Producto producto);

    void actualizar(Producto producto);
}
