package com.nexus.dao.jdbc;

import com.nexus.dao.DAOException;
import com.nexus.dao.IMovimientoDAO;
import com.nexus.model.Movimiento;

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

public class MovimientoDAOJdbc implements IMovimientoDAO {

    private static final String COLUMNAS = "SELECT id, sku, tipo, cantidad, fecha FROM movimiento";

    private final DataSource dataSource;

    public MovimientoDAOJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Movimiento> listarPorProducto(String sku) {
        return consultar(COLUMNAS + " WHERE sku = ? ORDER BY fecha", sku);
    }

    @Override
    public List<Movimiento> listarVentasDesde(String sku, LocalDateTime desde) {
        return consultar(COLUMNAS + " WHERE sku = ? AND tipo = 'VENTA' AND fecha >= ? ORDER BY fecha",
                sku, Timestamp.valueOf(desde));
    }

    @Override
    public void guardar(Movimiento movimiento) {
        String sql = "INSERT INTO movimiento (sku, tipo, cantidad, fecha) VALUES (?, ?, ?, ?)";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, movimiento.getSku());
            ps.setString(2, movimiento.getTipo().name());
            ps.setInt(3, movimiento.getCantidad());
            ps.setTimestamp(4, Timestamp.valueOf(movimiento.getFecha()));
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                claves.next();
                movimiento.setId(claves.getLong(1));
            }
        } catch (SQLException e) {
            throw new DAOException("No se pudo guardar el movimiento de " + movimiento.getSku(), e);
        }
    }

    private List<Movimiento> consultar(String sql, Object... parametros) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }
            List<Movimiento> movimientos = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    movimientos.add(mapear(rs));
                }
            }
            return movimientos;
        } catch (SQLException e) {
            throw new DAOException("No se pudieron consultar los movimientos", e);
        }
    }

    private Movimiento mapear(ResultSet rs) throws SQLException {
        Movimiento movimiento = new Movimiento();
        movimiento.setId(rs.getLong("id"));
        movimiento.setSku(rs.getString("sku"));
        movimiento.setTipo(Movimiento.Tipo.valueOf(rs.getString("tipo")));
        movimiento.setCantidad(rs.getInt("cantidad"));
        movimiento.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
        return movimiento;
    }
}
