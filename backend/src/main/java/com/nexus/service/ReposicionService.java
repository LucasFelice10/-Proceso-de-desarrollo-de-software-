package com.nexus.service;

import com.nexus.dao.IMovimientoDAO;
import com.nexus.dao.IPedidoDAO;
import com.nexus.dao.IProveedorDAO;
import com.nexus.model.Movimiento;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Lógica de reposición: cuánto se vende por día, cuánto tardan en llegar los pedidos y, con eso,
 * cuándo y cuánto pedir.
 */
public class ReposicionService {

    /** Días hacia atrás que se promedian para la venta diaria. */
    public static final int DIAS_VENTA = 30;

    /** z para un nivel de servicio del 95%: el stock de seguridad alcanza en 95 de cada 100 reposiciones. */
    public static final double Z_NIVEL_SERVICIO_95 = 1.65;

    private final IProveedorDAO proveedorDAO;
    private final IPedidoDAO pedidoDAO;
    private final IMovimientoDAO movimientoDAO;
    private final double z;

    public ReposicionService(IProveedorDAO proveedorDAO, IPedidoDAO pedidoDAO, IMovimientoDAO movimientoDAO) {
        this(proveedorDAO, pedidoDAO, movimientoDAO, Z_NIVEL_SERVICIO_95);
    }

    /** @param z factor del nivel de servicio (1.65 = 95%, 2.33 = 99%) */
    public ReposicionService(IProveedorDAO proveedorDAO, IPedidoDAO pedidoDAO, IMovimientoDAO movimientoDAO,
                             double z) {
        this.proveedorDAO = proveedorDAO;
        this.pedidoDAO = pedidoDAO;
        this.movimientoDAO = movimientoDAO;
        this.z = z;
    }

    /**
     * d: unidades vendidas por día. Suma las ventas desde las 0 h de hace {@value #DIAS_VENTA} días,
     * incluidas las de hoy para que una venta en vivo mueva el cálculo, y las divide por {@value #DIAS_VENTA}.
     */
    public double ventaDiariaPromedio(String sku) {
        LocalDateTime desde = LocalDate.now().minusDays(DIAS_VENTA).atStartOfDay();
        int vendidas = movimientoDAO.listarVentasDesde(sku, desde).stream()
                .mapToInt(Movimiento::getCantidad)
                .sum();
        return (double) vendidas / DIAS_VENTA;
    }

    /**
     * L: días promedio entre el envío y la recepción de las entregas del producto. Sin historial
     * se usa el promedio de los lead times que declaran los proveedores en sus ofertas.
     */
    public double leadTimePromedio(String sku) {
        List<Double> leadTimes = leadTimesReales(sku);
        if (leadTimes.isEmpty()) {
            return proveedorDAO.listarPorProducto(sku).stream()
                    .mapToInt(p -> p.getOferta(sku).getLeadTimeDias())
                    .average()
                    .orElse(0);
        }
        return promedio(leadTimes);
    }

    /**
     * σL: desvío estándar muestral (n - 1) de los lead times reales. Mide cuánto se atrasan o
     * adelantan las entregas; con menos de dos entregas no hay variación medible y vale 0.
     */
    public double desvioLeadTime(String sku) {
        List<Double> leadTimes = leadTimesReales(sku);
        if (leadTimes.size() < 2) {
            return 0;
        }
        double promedio = promedio(leadTimes);
        double sumaCuadrados = leadTimes.stream()
                .mapToDouble(l -> (l - promedio) * (l - promedio))
                .sum();
        return Math.sqrt(sumaCuadrados / (leadTimes.size() - 1));
    }

    /** SS = z · σL · d: unidades extra para seguir vendiendo si el proveedor se atrasa. */
    public double stockSeguridad(String sku) {
        return z * desvioLeadTime(sku) * ventaDiariaPromedio(sku);
    }

    /**
     * ROP = d · L + SS: lo que se vende mientras llega el pedido más el colchón por atrasos.
     * Cuando el stock baja a este valor hay que pedir. Se redondea porque se piden unidades enteras.
     */
    public int puntoDePedido(String sku) {
        return (int) Math.round(ventaDiariaPromedio(sku) * leadTimePromedio(sku) + stockSeguridad(sku));
    }

    /** Lead time real de cada entrega recibida, en días. */
    private List<Double> leadTimesReales(String sku) {
        return pedidoDAO.listarRecibidosPorProducto(sku).stream()
                .filter(p -> p.getFechaEnvio() != null && p.getFechaRecepcion() != null)
                .map(p -> Duration.between(p.getFechaEnvio(), p.getFechaRecepcion()).toHours() / 24.0)
                .toList();
    }

    private static double promedio(List<Double> valores) {
        return valores.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }
}
