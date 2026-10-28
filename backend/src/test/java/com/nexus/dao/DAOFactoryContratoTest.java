package com.nexus.dao;

import com.nexus.model.Movimiento;
import com.nexus.model.OfertaProveedor;
import com.nexus.model.PedidoReposicion;
import com.nexus.model.Producto;
import com.nexus.model.Proveedor;
import com.nexus.patterns.chain.ContextoVenta;
import com.nexus.patterns.observer.InventarioObservable;
import com.nexus.service.VentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Las mismas pruebas corren contra cada fábrica concreta: si pasan en todas, el negocio
 * puede cambiar de motor sin enterarse. Cada subclase arranca con los datos de demo cargados.
 */
abstract class DAOFactoryContratoTest {

    private DAOFactory fabrica;

    protected abstract DAOFactory crearFabrica() throws Exception;

    @BeforeEach
    void preparar() throws Exception {
        fabrica = crearFabrica();
    }

    // Productos

    @Test
    void cargaLosDatosDeDemo() {
        assertEquals(8, fabrica.getProductoDAO().listarTodos().size());
        assertEquals(4, fabrica.getProveedorDAO().listarTodos().size());
        assertEquals(32, fabrica.getPedidoDAO().listarTodos().size());

        Producto cerveza = fabrica.getProductoDAO().buscarPorSku("CERV-X").orElseThrow();
        assertEquals("Cerveza X 1 L", cerveza.getNombre());
        assertEquals(220, cerveza.getStockActual());
        assertEquals(120, cerveza.getUmbralCritico());
    }

    @Test
    void buscarProductoInexistenteDevuelveVacio() {
        assertTrue(fabrica.getProductoDAO().buscarPorSku("NO-EXISTE").isEmpty());
    }

    @Test
    void actualizarProductoPersisteElStock() {
        IProductoDAO dao = fabrica.getProductoDAO();
        Producto agua = dao.buscarPorSku("AGUA-CG").orElseThrow();
        agua.setStockActual(10);

        dao.actualizar(agua);

        assertEquals(10, dao.buscarPorSku("AGUA-CG").orElseThrow().getStockActual());
    }

    @Test
    void modificarUnProductoLeidoSinActualizarNoCambiaNada() {
        IProductoDAO dao = fabrica.getProductoDAO();
        dao.buscarPorSku("AGUA-CG").orElseThrow().setStockActual(0);

        assertEquals(48, dao.buscarPorSku("AGUA-CG").orElseThrow().getStockActual());
    }

    @Test
    void actualizarProductoInexistenteFalla() {
        Producto fantasma = new Producto("NO-EXISTE", "Nada", 1, 1);

        assertThrows(DAOException.class, () -> fabrica.getProductoDAO().actualizar(fantasma));
    }

    @Test
    void guardarProductoConSkuRepetidoFalla() {
        Producto repetido = new Producto("CERV-X", "Otra cerveza", 1, 1);

        assertThrows(DAOException.class, () -> fabrica.getProductoDAO().guardar(repetido));
    }

    // Proveedores

    @Test
    void proveedoresDeUnProductoVienenConSuOferta() {
        List<Proveedor> proveedores = fabrica.getProveedorDAO().listarPorProducto("CERV-X");

        assertEquals(List.of("Distribuidora del Sur", "Bebidas Express", "Mayorista Central", "Logística Norte"),
                proveedores.stream().map(Proveedor::getNombre).toList());
        OfertaProveedor express = proveedores.get(1).getOferta("CERV-X");
        assertEquals(1150.00, express.getPrecio(), 0.001);
        assertEquals(1, express.getLeadTimeDias());
    }

    @Test
    void soloAparecenLosProveedoresQueOfrecenElProducto() {
        List<Proveedor> proveedores = fabrica.getProveedorDAO().listarPorProducto("GAS-LIMA");

        assertEquals(List.of(1L, 3L), proveedores.stream().map(Proveedor::getId).toList());
    }

    @Test
    void guardarProveedorAsignaIdYGuardaSusOfertas() {
        Proveedor nuevo = new Proveedor(0, "Bebidas Oeste", "5491100000005");
        nuevo.getOfertas().add(new OfertaProveedor("CERV-X", 950.00, 2));

        fabrica.getProveedorDAO().guardar(nuevo);

        assertEquals(5, nuevo.getId());
        Proveedor leido = fabrica.getProveedorDAO().buscarPorId(5).orElseThrow();
        assertEquals("Bebidas Oeste", leido.getNombre());
        assertEquals(950.00, leido.getOferta("CERV-X").getPrecio(), 0.001);
        assertEquals(5, fabrica.getProveedorDAO().listarPorProducto("CERV-X").size());
    }

    // Pedidos

    @Test
    void historialDeEntregasTraeLasDemorasOrdenadasPorEnvio() {
        List<PedidoReposicion> entregas = fabrica.getPedidoDAO().listarRecibidosPorProducto("CERV-X");

        List<Long> demoras = entregas.stream()
                .map(p -> Duration.between(p.getFechaEnvio(), p.getFechaRecepcion()).toDays())
                .toList();
        assertEquals(List.of(3L, 8L, 4L, 1L, 3L, 7L, 5L, 7L), demoras);
        assertTrue(entregas.stream().allMatch(p -> "RECIBIDO".equals(p.getEstado())));
    }

    @Test
    void guardarYAvanzarUnPedido() {
        IPedidoDAO dao = fabrica.getPedidoDAO();
        PedidoReposicion pedido = new PedidoReposicion("CERV-X", 3, 150);

        dao.guardar(pedido);
        assertEquals(33, pedido.getId());

        PedidoReposicion leido = dao.buscarPorId(pedido.getId()).orElseThrow();
        assertEquals("GENERADO", leido.getEstado());
        assertNull(leido.getFechaEnvio());

        leido.setEstado("ENVIADO");
        leido.setFechaEnvio(LocalDateTime.now());
        dao.actualizar(leido);

        PedidoReposicion enviado = dao.buscarPorId(pedido.getId()).orElseThrow();
        assertEquals("ENVIADO", enviado.getEstado());
        assertNotNull(enviado.getFechaEnvio());
        assertEquals(9, dao.listarPorProducto("CERV-X").size());
        assertEquals(8, dao.listarRecibidosPorProducto("CERV-X").size());
    }

    @Test
    void actualizarPedidoInexistenteFalla() {
        PedidoReposicion fantasma = new PedidoReposicion("CERV-X", 1, 10);
        fantasma.setId(999);

        assertThrows(DAOException.class, () -> fabrica.getPedidoDAO().actualizar(fantasma));
    }

    // Movimientos

    @Test
    void ventasDeLosUltimosTreintaDias() {
        IMovimientoDAO dao = fabrica.getMovimientoDAO();
        List<Movimiento> mes = dao.listarVentasDesde("CERV-X", LocalDateTime.now().minusDays(31));

        assertEquals(30, mes.size());
        assertEquals(663, mes.stream().mapToInt(Movimiento::getCantidad).sum());
        assertTrue(mes.stream().allMatch(m -> m.getTipo() == Movimiento.Tipo.VENTA));
        assertEquals(6, dao.listarVentasDesde("CERV-X", LocalDateTime.now().minusDays(7)).size());
    }

    @Test
    void cadaEntregaDelHistorialTieneSuRecepcion() {
        long recepciones = fabrica.getMovimientoDAO().listarPorProducto("CERV-X").stream()
                .filter(m -> m.getTipo() == Movimiento.Tipo.RECEPCION)
                .count();

        assertEquals(8, recepciones);
    }

    @Test
    void guardarMovimientoAsignaIdYAparece() {
        Movimiento venta = new Movimiento("LIC-WHI", Movimiento.Tipo.VENTA, 4);

        fabrica.getMovimientoDAO().guardar(venta);

        assertNotEquals(0, venta.getId());
        List<Movimiento> hoy = fabrica.getMovimientoDAO()
                .listarVentasDesde("LIC-WHI", LocalDateTime.now().minusHours(1));
        assertEquals(List.of(4), hoy.stream().map(Movimiento::getCantidad).toList());
    }

    // El negocio no cambia al cambiar de motor

    @Test
    void ventaServiceFuncionaSobreLaFabrica() {
        IProductoDAO productoDAO = fabrica.getProductoDAO();
        VentaService ventas = new VentaService(productoDAO, new InventarioObservable(productoDAO));

        ContextoVenta venta = ventas.registrarVenta("CERV-X", 110);

        assertTrue(venta.isAlertaDisparada());
        assertEquals(110, productoDAO.buscarPorSku("CERV-X").orElseThrow().getStockActual());
    }
}
