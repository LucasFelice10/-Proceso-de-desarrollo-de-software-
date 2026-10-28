package com.nexus.dao.memoria;

import com.nexus.dao.IMovimientoDAO;
import com.nexus.model.Movimiento;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

public class MovimientoDAOMemoria implements IMovimientoDAO {

    private final Map<Long, Movimiento> movimientos = new ConcurrentSkipListMap<>();
    private final AtomicLong secuencia = new AtomicLong();

    @Override
    public List<Movimiento> listarPorProducto(String sku) {
        return movimientos.values().stream()
                .filter(m -> m.getSku().equals(sku))
                .sorted(Comparator.comparing(Movimiento::getFecha))
                .map(MovimientoDAOMemoria::copiar)
                .toList();
    }

    @Override
    public List<Movimiento> listarVentasDesde(String sku, LocalDateTime desde) {
        return movimientos.values().stream()
                .filter(m -> m.getSku().equals(sku)
                        && m.getTipo() == Movimiento.Tipo.VENTA
                        && !m.getFecha().isBefore(desde))
                .sorted(Comparator.comparing(Movimiento::getFecha))
                .map(MovimientoDAOMemoria::copiar)
                .toList();
    }

    @Override
    public void guardar(Movimiento movimiento) {
        movimiento.setId(secuencia.incrementAndGet());
        movimientos.put(movimiento.getId(), copiar(movimiento));
    }

    private static Movimiento copiar(Movimiento m) {
        Movimiento copia = new Movimiento();
        copia.setId(m.getId());
        copia.setSku(m.getSku());
        copia.setTipo(m.getTipo());
        copia.setCantidad(m.getCantidad());
        copia.setFecha(m.getFecha());
        return copia;
    }
}
