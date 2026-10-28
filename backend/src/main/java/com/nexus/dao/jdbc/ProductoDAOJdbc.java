package com.nexus.dao.jdbc;

import com.nexus.dao.DAOException;
import com.nexus.dao.IProductoDAO;
import com.nexus.model.Producto;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductoDAOJdbc implements IProductoDAO {

    private static final String COLUMNAS = "SELECT sku, nombre, stock_actual, umbral_critico FROM producto";

    private final DataSource dataSource;

    public ProductoDAOJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Producto> buscarPorSku(String sku) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(COLUMNAS + " WHERE sku = ?")) {
            ps.setString(1, sku);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DAOException("No se pudo buscar el producto " + sku, e);
        }
    }

    @Override
    public List<Producto> listarTodos() {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(COLUMNAS + " ORDER BY sku");
             ResultSet rs = ps.executeQuery()) {
            List<Producto> productos = new ArrayList<>();
            while (rs.next()) {
                productos.add(mapear(rs));
            }
            return productos;
        } catch (SQLException e) {
            throw new DAOException("No se pudieron listar los productos", e);
        }
    }

    @Override
    public void guardar(Producto producto) {
        String sql = "INSERT INTO producto (sku, nombre, stock_actual, umbral_critico) VALUES (?, ?, ?, ?)";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, producto.getSku());
            ps.setString(2, producto.getNombre());
            ps.setInt(3, producto.getStockActual());
            ps.setInt(4, producto.getUmbralCritico());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("No se pudo guardar el producto " + producto.getSku(), e);
        }
    }

    @Override
    public void actualizar(Producto producto) {
        String sql = "UPDATE producto SET nombre = ?, stock_actual = ?, umbral_critico = ? WHERE sku = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, producto.getNombre());
            ps.setInt(2, producto.getStockActual());
            ps.setInt(3, producto.getUmbralCritico());
            ps.setString(4, producto.getSku());
            if (ps.executeUpdate() == 0) {
                throw new DAOException("Producto inexistente: " + producto.getSku());
            }
        } catch (SQLException e) {
            throw new DAOException("No se pudo actualizar el producto " + producto.getSku(), e);
        }
    }

    private Producto mapear(ResultSet rs) throws SQLException {
        return new Producto(
                rs.getString("sku"),
                rs.getString("nombre"),
                rs.getInt("stock_actual"),
                rs.getInt("umbral_critico"));
    }
}
