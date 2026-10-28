package com.nexus.dao.jdbc;

import com.nexus.dao.DAOException;
import com.nexus.dao.IPedidoDAO;
import com.nexus.model.PedidoReposicion;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PedidoDAOJdbc implements IPedidoDAO {

    private static final String COLUMNAS = "SELECT id, sku, proveedor_id, cantidad, estado,"
            + " fecha_creacion, fecha_envio, fecha_recepcion FROM pedido_reposicion";

    private final DataSource dataSource;

    public PedidoDAOJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<PedidoReposicion> buscarPorId(long id) {
        return consultar(COLUMNAS + " WHERE id = ?", id).stream().findFirst();
    }

    @Override
    public List<PedidoReposicion> listarTodos() {
        return consultar(COLUMNAS + " ORDER BY id");
    }

    @Override
    public List<PedidoReposicion> listarPorProducto(String sku) {
        return consultar(COLUMNAS + " WHERE sku = ? ORDER BY id", sku);
    }

    @Override
    public List<PedidoReposicion> listarRecibidosPorProducto(String sku) {
        return consultar(COLUMNAS + " WHERE sku = ? AND estado = 'RECIBIDO' ORDER BY fecha_envio", sku);
    }

    @Override
    public void guardar(PedidoReposicion pedido) {
        String sql = "INSERT INTO pedido_reposicion (sku, proveedor_id, cantidad, estado,"
                + " fecha_creacion, fecha_envio, fecha_recepcion) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, pedido.getSku());
            ps.setLong(2, pedido.getProveedorId());
            ps.setInt(3, pedido.getCantidad());
            ps.setString(4, pedido.getEstado());
            ps.setTimestamp(5, aTimestamp(pedido.getFechaCreacion()));
            ps.setTimestamp(6, aTimestamp(pedido.getFechaEnvio()));
            ps.setTimestamp(7, aTimestamp(pedido.getFechaRecepcion()));
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                claves.next();
                pedido.setId(claves.getLong(1));
            }
        } catch (SQLException e) {
            throw new DAOException("No se pudo guardar el pedido de " + pedido.getSku(), e);
        }
    }

    @Override
    public void actualizar(PedidoReposicion pedido) {
        String sql = "UPDATE pedido_reposicion SET sku = ?, proveedor_id = ?, cantidad = ?, estado = ?,"
                + " fecha_creacion = ?, fecha_envio = ?, fecha_recepcion = ? WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, pedido.getSku());
            ps.setLong(2, pedido.getProveedorId());
            ps.setInt(3, pedido.getCantidad());
            ps.setString(4, pedido.getEstado());
            ps.setTimestamp(5, aTimestamp(pedido.getFechaCreacion()));
            ps.setTimestamp(6, aTimestamp(pedido.getFechaEnvio()));
            ps.setTimestamp(7, aTimestamp(pedido.getFechaRecepcion()));
            ps.setLong(8, pedido.getId());
            if (ps.executeUpdate() == 0) {
                throw new DAOException("Pedido inexistente: " + pedido.getId());
            }
        } catch (SQLException e) {
            throw new DAOException("No se pudo actualizar el pedido " + pedido.getId(), e);
        }
    }

    private List<PedidoReposicion> consultar(String sql, Object... parametros) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }
            List<PedidoReposicion> pedidos = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(mapear(rs));
                }
            }
            return pedidos;
        } catch (SQLException e) {
            throw new DAOException("No se pudieron consultar los pedidos", e);
        }
    }

    private PedidoReposicion mapear(ResultSet rs) throws SQLException {
        PedidoReposicion pedido = new PedidoReposicion();
        pedido.setId(rs.getLong("id"));
        pedido.setSku(rs.getString("sku"));
        pedido.setProveedorId(rs.getLong("proveedor_id"));
        pedido.setCantidad(rs.getInt("cantidad"));
        pedido.setEstado(rs.getString("estado"));
        pedido.setFechaCreacion(aFecha(rs.getTimestamp("fecha_creacion")));
        pedido.setFechaEnvio(aFecha(rs.getTimestamp("fecha_envio")));
        pedido.setFechaRecepcion(aFecha(rs.getTimestamp("fecha_recepcion")));
        return pedido;
    }

    private static Timestamp aTimestamp(LocalDateTime fecha) {
        return fecha == null ? null : Timestamp.valueOf(fecha);
    }

    private static LocalDateTime aFecha(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
