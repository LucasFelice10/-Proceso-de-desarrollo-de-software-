package com.nexus.patterns;

import com.nexus.ProductoDAOEnMemoria;
import com.nexus.model.Producto;
import com.nexus.patterns.observer.InventarioObservable;
import com.nexus.patterns.observer.Observador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InventarioObservableTest {

    private ProductoDAOEnMemoria dao;
    private InventarioObservable inventario;
    private List<Integer> avisos;
    private Observador observador;

    @BeforeEach
    void preparar() {
        dao = new ProductoDAOEnMemoria();
        dao.guardar(new Producto("CERV-X", "Cerveza X", 100, 20));
        inventario = new InventarioObservable(dao);
        avisos = new ArrayList<>();
        observador = producto -> avisos.add(producto.getStockActual());
        inventario.agregarObservador(observador);
    }

    @Test
    void descontarStockActualizaYNotificaAlObservador() {
        inventario.descontarStock("CERV-X", 30);

        assertEquals(70, dao.buscarPorSku("CERV-X").orElseThrow().getStockActual());
        assertEquals(List.of(70), avisos);
    }

    @Test
    void agregarStockSumaYNotifica() {
        inventario.agregarStock("CERV-X", 50);

        assertEquals(150, dao.buscarPorSku("CERV-X").orElseThrow().getStockActual());
        assertEquals(List.of(150), avisos);
    }

    @Test
    void observadorQuitadoYaNoRecibeAvisos() {
        inventario.quitarObservador(observador);

        inventario.descontarStock("CERV-X", 10);

        assertTrue(avisos.isEmpty());
    }

    @Test
    void todosLosObservadoresSonNotificados() {
        List<Integer> otros = new ArrayList<>();
        inventario.agregarObservador(p -> otros.add(p.getStockActual()));

        inventario.descontarStock("CERV-X", 5);

        assertEquals(List.of(95), avisos);
        assertEquals(List.of(95), otros);
    }

    @Test
    void productoInexistenteTiraExcepcionYNoNotifica() {
        assertThrows(NoSuchElementException.class, () -> inventario.descontarStock("NO-EXISTE", 1));
        assertTrue(avisos.isEmpty());
    }
}
