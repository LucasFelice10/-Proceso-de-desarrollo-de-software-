package com.nexus.dao.memoria;

import com.nexus.dao.DAOFactory;
import com.nexus.model.Movimiento;
import com.nexus.model.OfertaProveedor;
import com.nexus.model.PedidoReposicion;
import com.nexus.model.Producto;
import com.nexus.model.Proveedor;

import java.time.LocalDateTime;

/**
 * Los mismos datos de resources/data.sql, cargados a través de los DAOs.
 * Si se cambia algo en data.sql, hay que cambiarlo acá también.
 */
public final class DatosDemo {

    private static final Object[][] PRODUCTOS = {
            {"CERV-X", "Cerveza X 1 L", 220, 120},
            {"CERV-RUB", "Cerveza rubia lata 473 ml", 260, 120},
            {"GAS-COLA", "Gaseosa cola 2,25 L", 220, 100},
            {"GAS-LIMA", "Gaseosa lima-limón 2,25 L", 90, 50},
            {"AGUA-SG", "Agua mineral sin gas 2 L", 300, 130},
            {"AGUA-CG", "Agua mineral con gas 1,5 L", 48, 40},
            {"LIC-VOD", "Vodka 1 L", 40, 20},
            {"LIC-WHI", "Whisky 750 ml", 25, 15},
    };

    private static final String[][] PROVEEDORES = {
            {"Distribuidora del Sur", "5491100000001"},
            {"Bebidas Express", "5491100000002"},
            {"Mayorista Central", "5491100000003"},
            {"Logística Norte", "5491100000004"},
    };

    /** proveedor (1 a 4), sku, precio, lead time en días. */
    private static final Object[][] OFERTAS = {
            {1, "CERV-X", 900.00, 6}, {2, "CERV-X", 1150.00, 1}, {3, "CERV-X", 990.00, 3}, {4, "CERV-X", 980.00, 5},
            {1, "CERV-RUB", 750.00, 5}, {2, "CERV-RUB", 950.00, 1}, {3, "CERV-RUB", 820.00, 3},
            {1, "GAS-COLA", 1400.00, 4}, {2, "GAS-COLA", 1750.00, 1}, {4, "GAS-COLA", 1500.00, 3},
            {1, "GAS-LIMA", 1300.00, 4}, {3, "GAS-LIMA", 1450.00, 2},
            {2, "AGUA-SG", 780.00, 1}, {3, "AGUA-SG", 650.00, 2}, {4, "AGUA-SG", 600.00, 4},
            {1, "AGUA-CG", 560.00, 5}, {2, "AGUA-CG", 720.00, 1}, {3, "AGUA-CG", 610.00, 3},
            {2, "LIC-VOD", 7800.00, 2}, {3, "LIC-VOD", 6900.00, 3}, {4, "LIC-VOD", 6500.00, 6},
            {1, "LIC-WHI", 15500.00, 7}, {2, "LIC-WHI", 18900.00, 2}, {3, "LIC-WHI", 16800.00, 4},
    };

    /** Historial de entregas: sku, proveedor, cantidad, días desde el envío, días que tardó. */
    private static final Object[][] ENTREGAS = {
            {"CERV-X", 3, 150, 58, 3}, {"CERV-X", 1, 180, 52, 8}, {"CERV-X", 3, 150, 44, 4},
            {"CERV-X", 2, 120, 37, 1}, {"CERV-X", 3, 150, 30, 3}, {"CERV-X", 4, 160, 24, 7},
            {"CERV-X", 3, 150, 16, 5}, {"CERV-X", 1, 180, 10, 7},
            {"CERV-RUB", 3, 200, 50, 3}, {"CERV-RUB", 1, 240, 41, 6}, {"CERV-RUB", 3, 200, 27, 4},
            {"CERV-RUB", 2, 150, 15, 1},
            {"GAS-COLA", 4, 180, 55, 3}, {"GAS-COLA", 1, 200, 40, 5}, {"GAS-COLA", 4, 180, 26, 4},
            {"GAS-COLA", 2, 120, 12, 2},
            {"GAS-LIMA", 1, 90, 48, 4}, {"GAS-LIMA", 3, 80, 33, 2}, {"GAS-LIMA", 1, 90, 19, 6},
            {"AGUA-SG", 3, 250, 45, 2}, {"AGUA-SG", 4, 280, 36, 5}, {"AGUA-SG", 3, 250, 22, 2},
            {"AGUA-SG", 2, 200, 9, 1},
            {"AGUA-CG", 3, 70, 47, 3}, {"AGUA-CG", 1, 80, 31, 7}, {"AGUA-CG", 3, 70, 18, 3},
            {"LIC-VOD", 3, 30, 60, 3}, {"LIC-VOD", 4, 40, 39, 8}, {"LIC-VOD", 2, 25, 20, 2},
            {"LIC-WHI", 3, 20, 58, 4}, {"LIC-WHI", 1, 25, 34, 9}, {"LIC-WHI", 2, 15, 14, 2},
    };

    /** Ventas de los últimos 30 días: sku, base, paso, rango (cantidad = base + dia * paso % rango). */
    private static final Object[][] VENTAS = {
            {"CERV-X", 18, 7, 9},
            {"CERV-RUB", 30, 5, 11},
            {"GAS-COLA", 25, 3, 10},
            {"GAS-LIMA", 12, 7, 6},
            {"AGUA-SG", 35, 11, 13},
            {"AGUA-CG", 10, 3, 5},
            {"LIC-VOD", 3, 1, 4},
            {"LIC-WHI", 2, 2, 3},
    };

    private DatosDemo() {
    }

    public static void cargar(DAOFactory fabrica) {
        LocalDateTime ahora = LocalDateTime.now();

        for (Object[] p : PRODUCTOS) {
            fabrica.getProductoDAO().guardar(new Producto((String) p[0], (String) p[1], (int) p[2], (int) p[3]));
        }

        for (int i = 0; i < PROVEEDORES.length; i++) {
            Proveedor proveedor = new Proveedor(0, PROVEEDORES[i][0], PROVEEDORES[i][1]);
            for (Object[] o : OFERTAS) {
                if ((int) o[0] == i + 1) {
                    proveedor.getOfertas().add(new OfertaProveedor((String) o[1], (double) o[2], (int) o[3]));
                }
            }
            fabrica.getProveedorDAO().guardar(proveedor);
        }

        for (Object[] e : ENTREGAS) {
            LocalDateTime envio = ahora.minusDays((int) e[3]);
            PedidoReposicion pedido = new PedidoReposicion((String) e[0], (int) e[1], (int) e[2]);
            pedido.setEstado("RECIBIDO");
            pedido.setFechaCreacion(envio);
            pedido.setFechaEnvio(envio);
            pedido.setFechaRecepcion(envio.plusDays((int) e[4]));
            fabrica.getPedidoDAO().guardar(pedido);

            Movimiento recepcion = new Movimiento(pedido.getSku(), Movimiento.Tipo.RECEPCION, pedido.getCantidad());
            recepcion.setFecha(pedido.getFechaRecepcion());
            fabrica.getMovimientoDAO().guardar(recepcion);
        }

        for (Object[] v : VENTAS) {
            for (int dia = 1; dia <= 30; dia++) {
                int cantidad = (int) v[1] + dia * (int) v[2] % (int) v[3];
                Movimiento venta = new Movimiento((String) v[0], Movimiento.Tipo.VENTA, cantidad);
                venta.setFecha(ahora.minusDays(dia));
                fabrica.getMovimientoDAO().guardar(venta);
            }
        }
    }
}
