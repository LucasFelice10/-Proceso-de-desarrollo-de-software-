package com.nexus.dao.memoria;

import com.nexus.dao.DAOException;
import com.nexus.dao.IProductoDAO;
import com.nexus.model.Producto;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Guarda copias de los productos, igual que una base: modificar un objeto leído
 * no cambia nada hasta que se llama a actualizar().
 */
public class ProductoDAOMemoria implements IProductoDAO {

    private final Map<String, Producto> productos = new ConcurrentSkipListMap<>();

    @Override
    public Optional<Producto> buscarPorSku(String sku) {
        return Optional.ofNullable(productos.get(sku)).map(ProductoDAOMemoria::copiar);
    }

    @Override
    public List<Producto> listarTodos() {
        return productos.values().stream().map(ProductoDAOMemoria::copiar).toList();
    }

    @Override
    public void guardar(Producto producto) {
        if (productos.putIfAbsent(producto.getSku(), copiar(producto)) != null) {
            throw new DAOException("Ya existe el producto " + producto.getSku());
        }
    }

    @Override
    public void actualizar(Producto producto) {
        if (productos.replace(producto.getSku(), copiar(producto)) == null) {
            throw new DAOException("Producto inexistente: " + producto.getSku());
        }
    }

    private static Producto copiar(Producto p) {
        return new Producto(p.getSku(), p.getNombre(), p.getStockActual(), p.getUmbralCritico());
    }
}
