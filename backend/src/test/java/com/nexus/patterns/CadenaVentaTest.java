package com.nexus.patterns;

import com.nexus.ProductoDAOEnMemoria;
import com.nexus.model.Producto;
import com.nexus.patterns.chain.ContextoVenta;
import com.nexus.patterns.chain.StockInsuficienteException;
import com.nexus.patterns.observer.InventarioObservable;
import com.nexus.service.VentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CadenaVentaTest {

    private ProductoDAOEnMemoria dao;
    private VentaService ventaService;
    private List<Integer> avisos;

    @BeforeEach
    void preparar() {
        dao = new ProductoDAOEnMemoria();
        dao.guardar(new Producto("CERV-X", "Cerveza X", 100, 20));
        InventarioObservable inventario = new InventarioObservable(dao);
        avisos = new ArrayList<>();
        inventario.agregarObservador(producto -> avisos.add(producto.getStockActual()));
        ventaService = new VentaService(dao, inventario);
    }

    private int stock() {
        return dao.buscarPorSku("CERV-X").orElseThrow().getStockActual();
    }

    @Test
    void ventaValidaDescuentaStockYNotifica() {
        ContextoVenta resultado = ventaService.registrarVenta("CERV-X", 10);

        assertEquals(90, stock());
        assertEquals(90, resultado.getProducto().getStockActual());
        assertEquals(List.of(90), avisos);
        assertFalse(resultado.isUmbralCruzado());
        assertFalse(resultado.isAlertaDisparada());
    }

    @Test
    void ventaQueCruzaElUmbralDisparaAlerta() {
        ContextoVenta resultado = ventaService.registrarVenta("CERV-X", 85);

        assertEquals(15, stock());
        assertTrue(resultado.isUmbralCruzado());
        assertTrue(resultado.isAlertaDisparada());
    }

    @Test
    void ventaQueQuedaJustoEnElUmbralDisparaAlerta() {
        ContextoVenta resultado = ventaService.registrarVenta("CERV-X", 80);

        assertEquals(20, stock());
        assertTrue(resultado.isAlertaDisparada());
    }

    @Test
    void ventaConStockYaBajoElUmbralNoRepiteLaAlerta() {
        ventaService.registrarVenta("CERV-X", 85);

        ContextoVenta segunda = ventaService.registrarVenta("CERV-X", 5);

        assertEquals(10, stock());
        assertFalse(segunda.isUmbralCruzado());
        assertFalse(segunda.isAlertaDisparada());
    }

    @Test
    void stockInsuficienteCortaLaCadenaSinTocarNada() {
        assertThrows(StockInsuficienteException.class, () -> ventaService.registrarVenta("CERV-X", 101));

        assertEquals(100, stock());
        assertTrue(avisos.isEmpty());
    }

    @Test
    void sePuedeVenderExactamenteTodoElStock() {
        ContextoVenta resultado = ventaService.registrarVenta("CERV-X", 100);

        assertEquals(0, stock());
        assertTrue(resultado.isAlertaDisparada());
    }

    @Test
    void cantidadNoPositivaEsRechazada() {
        assertThrows(IllegalArgumentException.class, () -> ventaService.registrarVenta("CERV-X", 0));
        assertThrows(IllegalArgumentException.class, () -> ventaService.registrarVenta("CERV-X", -5));
        assertEquals(100, stock());
    }

    @Test
    void productoInexistenteEsRechazado() {
        assertThrows(NoSuchElementException.class, () -> ventaService.registrarVenta("NO-EXISTE", 1));
    }
}
