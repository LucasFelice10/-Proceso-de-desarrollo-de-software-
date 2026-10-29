package com.nexus.service;

import com.nexus.dao.DAOFactory;
import com.nexus.dao.MemoriaDAOFactory;
import com.nexus.model.Movimiento;
import com.nexus.model.PedidoReposicion;
import com.nexus.model.Producto;
import com.nexus.patterns.strategy.EstrategiaBalanceada;
import com.nexus.patterns.strategy.EstrategiaPrecio;
import com.nexus.patterns.strategy.EstrategiaVelocidad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Sobre los datos de la demo, con Cerveza X ya bajo el umbral (stock 110, ROP 194). */
class GeneradorPedidosTest {

    private DAOFactory fabrica;
    private GeneradorPedidos generador;

    @BeforeEach
    void preparar() {
        fabrica = MemoriaDAOFactory.conDatosDemo();
        Producto cerveza = fabrica.getProductoDAO().buscarPorSku("CERV-X").orElseThrow();
        cerveza.setStockActual(110);
        fabrica.getProductoDAO().actualizar(cerveza);

        ReposicionService reposicion = new ReposicionService(fabrica.getProductoDAO(),
                fabrica.getProveedorDAO(), fabrica.getPedidoDAO(), fabrica.getMovimientoDAO());
        generador = new GeneradorPedidos(fabrica.getProveedorDAO(), fabrica.getPedidoDAO(), reposicion);
    }

    @Test
    void porDefectoUsaLaEstrategiaBalanceada() {
        assertInstanceOf(EstrategiaBalanceada.class, generador.getEstrategia());
    }

    @Test
    void oneClickConPerfilPrecioLePideADistribuidoraDelSur() {
        generador.setEstrategia(new EstrategiaPrecio());

        assertEquals(1, generador.ejecutarConexionOneClick("CERV-X").getProveedorId());
    }

    @Test
    void oneClickConPerfilVelocidadLePideABebidasExpress() {
        generador.setEstrategia(new EstrategiaVelocidad());

        assertEquals(2, generador.ejecutarConexionOneClick("CERV-X").getProveedorId());
    }

    @Test
    void oneClickConPerfilBalanceadoLePideAMayoristaCentral() {
        assertEquals(3, generador.ejecutarConexionOneClick("CERV-X").getProveedorId());
    }

    @Test
    void elPedidoQuedaGuardadoGeneradoConLaCantidadSugerida() {
        PedidoReposicion pedido = generador.ejecutarConexionOneClick("CERV-X");

        assertEquals(33, pedido.getId()); // después de las 32 entregas del historial
        PedidoReposicion guardado = fabrica.getPedidoDAO().buscarPorId(pedido.getId()).orElseThrow();
        assertEquals("CERV-X", guardado.getSku());
        assertEquals(239, guardado.getCantidad());
        assertEquals("GENERADO", guardado.getEstado());
        assertNotNull(guardado.getFechaCreacion());
    }

    @Test
    void unSegundoOneClickNoDuplicaElPedido() {
        generador.ejecutarConexionOneClick("CERV-X");

        assertThrows(IllegalStateException.class, () -> generador.ejecutarConexionOneClick("CERV-X"));
        assertEquals(33, fabrica.getPedidoDAO().listarTodos().size());
    }

    @Test
    void conStockSobreElPuntoDePedidoNoGeneraPedido() {
        // Agua sin gas arranca con 300 unidades y ROP 221
        assertThrows(IllegalStateException.class, () -> generador.ejecutarConexionOneClick("AGUA-SG"));
        assertEquals(32, fabrica.getPedidoDAO().listarTodos().size());
    }

    @Test
    void productoInexistenteFalla() {
        assertThrows(NoSuchElementException.class, () -> generador.ejecutarConexionOneClick("NO-EXISTE"));
    }

    @Test
    void productoQueNingunProveedorOfreceFalla() {
        fabrica.getProductoDAO().guardar(new Producto("SODA", "Soda 2 L", 0, 10));
        fabrica.getMovimientoDAO().guardar(new Movimiento("SODA", Movimiento.Tipo.VENTA, 30));

        assertThrows(NoSuchElementException.class, () -> generador.ejecutarConexionOneClick("SODA"));
        assertEquals(32, fabrica.getPedidoDAO().listarTodos().size());
    }

    @Test
    void laEstrategiaNoPuedeSerNula() {
        assertThrows(NullPointerException.class, () -> generador.setEstrategia(null));
        assertInstanceOf(EstrategiaBalanceada.class, generador.getEstrategia());
    }
}
