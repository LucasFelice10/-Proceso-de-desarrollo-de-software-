package com.nexus.dao;

import com.nexus.model.Movimiento;

import java.time.LocalDateTime;
import java.util.List;

public interface IMovimientoDAO {

    List<Movimiento> listarPorProducto(String sku);

    /** Ventas del producto desde la fecha indicada; sirve para calcular la venta diaria promedio. */
    List<Movimiento> listarVentasDesde(String sku, LocalDateTime desde);

    /** Inserta el movimiento y le asigna el id generado. */
    void guardar(Movimiento movimiento);
}
