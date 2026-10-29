package com.nexus.service;

import com.nexus.dao.DAOFactory;
import com.nexus.dao.MemoriaDAOFactory;
import com.nexus.model.Movimiento;
import com.nexus.model.OfertaProveedor;
import com.nexus.model.PedidoReposicion;
import com.nexus.model.Producto;
import com.nexus.model.Proveedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Caso armado a mano para que las cuentas den redondas: se venden 10 unidades por día y las
 * entregas tardaron 3, 5 y 7 días.
 *
 * <pre>
 * d = 10    L = 5    σL = √((4 + 0 + 4) / 2) = 2
 * SS  = 1.65 · 2 · 10 = 33
 * ROP = 10 · 5 + 33   = 83
 * </pre>
 */
class ReposicionServiceTest {

    private static final String SKU = "JUGO";

    private DAOFactory fabrica;
    private ReposicionService reposicion;

    @BeforeEach
    void preparar() {
        fabrica = new MemoriaDAOFactory();
        fabrica.getProductoDAO().guardar(new Producto(SKU, "Jugo de naranja 1 L", 60, 30));
        for (int dia = 1; dia <= 30; dia++) {
            venta(10, dia);
        }
        venta(500, 40); // fuera de la ventana de 30 días
        entrega(3, 50);
        entrega(5, 35);
        entrega(7, 20);
        reposicion = crear(fabrica);
    }

    private static ReposicionService crear(DAOFactory fabrica) {
        return new ReposicionService(fabrica.getProductoDAO(), fabrica.getProveedorDAO(),
                fabrica.getPedidoDAO(), fabrica.getMovimientoDAO());
    }

    private void venta(int cantidad, int haceDias) {
        Movimiento venta = new Movimiento(SKU, Movimiento.Tipo.VENTA, cantidad);
        venta.setFecha(LocalDateTime.now().minusDays(haceDias));
        fabrica.getMovimientoDAO().guardar(venta);
    }

    private void entrega(int demoraDias, int haceDias) {
        PedidoReposicion pedido = new PedidoReposicion(SKU, 1, 100);
        pedido.setEstado("RECIBIDO");
        pedido.setFechaEnvio(LocalDateTime.now().minusDays(haceDias));
        pedido.setFechaRecepcion(pedido.getFechaEnvio().plusDays(demoraDias));
        fabrica.getPedidoDAO().guardar(pedido);
    }

    private void pedidoEnCamino(String estado, int cantidad) {
        PedidoReposicion pedido = new PedidoReposicion(SKU, 1, cantidad);
        pedido.setEstado(estado);
        fabrica.getPedidoDAO().guardar(pedido);
    }

    private void stock(int unidades) {
        Producto producto = fabrica.getProductoDAO().buscarPorSku(SKU).orElseThrow();
        producto.setStockActual(unidades);
        fabrica.getProductoDAO().actualizar(producto);
    }

    // Insumos: d, L y σL

    @Test
    void ventaDiariaPromediaLosUltimosTreintaDias() {
        assertEquals(10.0, reposicion.ventaDiariaPromedio(SKU), 1e-9);
    }

    @Test
    void lasVentasDeHoyEntranEnElPromedio() {
        venta(30, 0);

        assertEquals(11.0, reposicion.ventaDiariaPromedio(SKU), 1e-9);
    }

    @Test
    void leadTimePromedioSaleDelHistorialDeEntregas() {
        assertEquals(5.0, reposicion.leadTimePromedio(SKU), 1e-9);
    }

    @Test
    void desvioEstandarMuestralDeLasDemoras() {
        assertEquals(2.0, reposicion.desvioLeadTime(SKU), 1e-9);
    }

    @Test
    void conUnaSolaEntregaNoHayDesvio() {
        fabrica = new MemoriaDAOFactory();
        fabrica.getProductoDAO().guardar(new Producto(SKU, "Jugo", 60, 30));
        entrega(4, 10);
        reposicion = crear(fabrica);

        assertEquals(4.0, reposicion.leadTimePromedio(SKU), 1e-9);
        assertEquals(0.0, reposicion.desvioLeadTime(SKU), 1e-9);
    }

    @Test
    void sinHistorialSeUsaElLeadTimeDeLasOfertas() {
        fabrica = new MemoriaDAOFactory();
        fabrica.getProductoDAO().guardar(new Producto(SKU, "Jugo", 60, 30));
        for (int dia = 1; dia <= 30; dia++) {
            venta(10, dia);
        }
        for (int leadTime : new int[]{2, 4}) {
            Proveedor proveedor = new Proveedor(0, "Proveedor de " + leadTime + " días", "5491100000000");
            proveedor.getOfertas().add(new OfertaProveedor(SKU, 1000.00, leadTime));
            fabrica.getProveedorDAO().guardar(proveedor);
        }
        reposicion = crear(fabrica);

        assertEquals(3.0, reposicion.leadTimePromedio(SKU), 1e-9);
        assertEquals(0.0, reposicion.stockSeguridad(SKU), 1e-9);
        assertEquals(30, reposicion.puntoDePedido(SKU));
    }

    // SS y ROP

    @Test
    void stockDeSeguridadYPuntoDePedido() {
        assertEquals(33.0, reposicion.stockSeguridad(SKU), 1e-9);
        assertEquals(83, reposicion.puntoDePedido(SKU));
    }

    @Test
    void unNivelDeServicioMasAltoSubeElPuntoDePedido() {
        ReposicionService al99 = new ReposicionService(fabrica.getProductoDAO(), fabrica.getProveedorDAO(),
                fabrica.getPedidoDAO(), fabrica.getMovimientoDAO(), 2.33);

        assertEquals(46.6, al99.stockSeguridad(SKU), 1e-9); // 2.33 · 2 · 10
        assertEquals(97, al99.puntoDePedido(SKU));          // 50 + 46.6, redondeado
    }

    @Test
    void entregasMasIrregularesPidenAntes() {
        entrega(15, 5); // L = 7.5, σL ≈ 5.26

        assertTrue(reposicion.puntoDePedido(SKU) > 83);
    }

    // Cantidad sugerida

    @Test
    void cantidadSugeridaCompletaHastaElRopMasUnaSemanaDeVenta() {
        // Stock 60 <= ROP 83: se pide hasta 83 + 7 · 10 = 153
        assertEquals(93, reposicion.cantidadSugerida(SKU));
    }

    @Test
    void justoEnElPuntoDePedidoYaSePide() {
        stock(83);

        assertEquals(70, reposicion.cantidadSugerida(SKU));
    }

    @Test
    void porEncimaDelPuntoDePedidoNoSePide() {
        stock(84);

        assertEquals(0, reposicion.cantidadSugerida(SKU));
    }

    @Test
    void lasUnidadesEnCaminoCuentanComoStock() {
        pedidoEnCamino("ENVIADO", 20); // posición 80: todavía hay que pedir

        assertEquals(73, reposicion.cantidadSugerida(SKU));

        pedidoEnCamino("GENERADO", 10); // posición 90: ya alcanza

        assertEquals(0, reposicion.cantidadSugerida(SKU));
    }

    @Test
    void productoInexistenteFalla() {
        assertThrows(NoSuchElementException.class, () -> reposicion.cantidadSugerida("NO-EXISTE"));
    }

    // Datos de la demo

    @Test
    void ejemploNumericoDeCervezaX() {
        // 663 unidades en 30 días; demoras 3, 8, 4, 1, 3, 7, 5 y 7 días
        DAOFactory demo = MemoriaDAOFactory.conDatosDemo();
        ReposicionService servicio = crear(demo);

        assertEquals(22.1, servicio.ventaDiariaPromedio("CERV-X"), 1e-9);
        assertEquals(4.75, servicio.leadTimePromedio("CERV-X"), 1e-9);
        assertEquals(Math.sqrt(41.5 / 7), servicio.desvioLeadTime("CERV-X"), 1e-9); // ≈ 2.43
        assertEquals(88.79, servicio.stockSeguridad("CERV-X"), 0.01);
        assertEquals(194, servicio.puntoDePedido("CERV-X"));                          // 104.98 + 88.79
        assertEquals(0, servicio.cantidadSugerida("CERV-X"));                         // stock 220 > 194

        Producto cerveza = demo.getProductoDAO().buscarPorSku("CERV-X").orElseThrow();
        cerveza.setStockActual(110);
        demo.getProductoDAO().actualizar(cerveza);

        assertEquals(239, servicio.cantidadSugerida("CERV-X"));                       // 194 + 155 - 110
    }

    @Test
    void enLaDemoElRopSiempreQuedaPorEncimaDelUmbralCritico() {
        // Si una fila se pone en rojo (stock <= umbral), el One-Click siempre tiene algo para pedir.
        DAOFactory demo = MemoriaDAOFactory.conDatosDemo();
        ReposicionService servicio = crear(demo);

        for (Producto producto : demo.getProductoDAO().listarTodos()) {
            assertTrue(servicio.puntoDePedido(producto.getSku()) > producto.getUmbralCritico(),
                    producto.getSku());
        }
    }
}
