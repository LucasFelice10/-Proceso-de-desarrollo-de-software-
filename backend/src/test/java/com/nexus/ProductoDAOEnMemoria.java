package com.nexus;

import com.nexus.dao.IProductoDAO;
import com.nexus.model.Producto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** DAO falso para probar los patrones sin base de datos. */
public class ProductoDAOEnMemoria implements IProductoDAO {

    private final Map<String, Producto> productos = new LinkedHashMap<>();

    @Override
    public Optional<Producto> buscarPorSku(String sku) {
        return Optional.ofNullable(productos.get(sku));
    }

    @Override
    public List<Producto> listarTodos() {
        return new ArrayList<>(productos.values());
    }

    @Override
    public void guardar(Producto producto) {
        productos.put(producto.getSku(), producto);
    }

    @Override
    public void actualizar(Producto producto) {
        productos.put(producto.getSku(), producto);
    }
}
