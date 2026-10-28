package com.nexus.dao.jdbc;

import com.nexus.dao.DAOException;
import com.nexus.dao.IProveedorDAO;
import com.nexus.model.OfertaProveedor;
import com.nexus.model.Proveedor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Cada proveedor se devuelve con todas sus ofertas (tabla oferta_proveedor) cargadas. */
public class ProveedorDAOJdbc implements IProveedorDAO {

    private final DataSource dataSource;

    public ProveedorDAOJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Proveedor> buscarPorId(long id) {
        return consultar("SELECT id, nombre, contacto FROM proveedor WHERE id = ?", id).stream().findFirst();
    }

    @Override
    public List<Proveedor> listarTodos() {
        return consultar("SELECT id, nombre, contacto FROM proveedor ORDER BY id");
    }

    @Override
    public List<Proveedor> listarPorProducto(String sku) {
        return consultar("SELECT p.id, p.nombre, p.contacto FROM proveedor p"
                + " JOIN oferta_proveedor o ON o.proveedor_id = p.id"
                + " WHERE o.sku = ? ORDER BY p.id", sku);
    }

    /** Inserta el proveedor con sus ofertas en una sola transacción y le asigna el id generado. */
    @Override
    public void guardar(Proveedor proveedor) {
        try (Connection con = dataSource.getConnection()) {
            con.setAutoCommit(false);
            try {
                insertarProveedor(con, proveedor);
                insertarOfertas(con, proveedor);
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DAOException("No se pudo guardar el proveedor " + proveedor.getNombre(), e);
        }
    }

    private List<Proveedor> consultar(String sql, Object... parametros) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }
            Map<Long, Proveedor> proveedores = new LinkedHashMap<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Proveedor proveedor = new Proveedor(
                            rs.getLong("id"), rs.getString("nombre"), rs.getString("contacto"));
                    proveedores.put(proveedor.getId(), proveedor);
                }
            }
            cargarOfertas(con, proveedores);
            return new ArrayList<>(proveedores.values());
        } catch (SQLException e) {
            throw new DAOException("No se pudieron consultar los proveedores", e);
        }
    }

    private void cargarOfertas(Connection con, Map<Long, Proveedor> proveedores) throws SQLException {
        if (proveedores.isEmpty()) {
            return;
        }
        String marcadores = String.join(", ", Collections.nCopies(proveedores.size(), "?"));
        String sql = "SELECT proveedor_id, sku, precio, lead_time_dias FROM oferta_proveedor"
                + " WHERE proveedor_id IN (" + marcadores + ") ORDER BY proveedor_id, sku";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            int i = 1;
            for (Long id : proveedores.keySet()) {
                ps.setLong(i++, id);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    proveedores.get(rs.getLong("proveedor_id")).getOfertas().add(new OfertaProveedor(
                            rs.getString("sku"), rs.getDouble("precio"), rs.getInt("lead_time_dias")));
                }
            }
        }
    }

    private void insertarProveedor(Connection con, Proveedor proveedor) throws SQLException {
        String sql = "INSERT INTO proveedor (nombre, contacto) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, proveedor.getNombre());
            ps.setString(2, proveedor.getContacto());
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                claves.next();
                proveedor.setId(claves.getLong(1));
            }
        }
    }

    private void insertarOfertas(Connection con, Proveedor proveedor) throws SQLException {
        String sql = "INSERT INTO oferta_proveedor (proveedor_id, sku, precio, lead_time_dias) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (OfertaProveedor oferta : proveedor.getOfertas()) {
                ps.setLong(1, proveedor.getId());
                ps.setString(2, oferta.getSku());
                ps.setDouble(3, oferta.getPrecio());
                ps.setInt(4, oferta.getLeadTimeDias());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}
