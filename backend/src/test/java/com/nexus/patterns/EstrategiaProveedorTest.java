package com.nexus.patterns;

import com.nexus.model.OfertaProveedor;
import com.nexus.model.Proveedor;
import com.nexus.patterns.strategy.EstrategiaBalanceada;
import com.nexus.patterns.strategy.EstrategiaPrecio;
import com.nexus.patterns.strategy.EstrategiaVelocidad;
import com.nexus.patterns.strategy.IEstrategiaProveedor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EstrategiaProveedorTest {

    private static final String SKU = "CERV-X";

    // Las cuatro ofertas de Cerveza X de data.sql
    private final Proveedor sur = proveedor(1, SKU, 900.00, 6);
    private final Proveedor express = proveedor(2, SKU, 1150.00, 1);
    private final Proveedor central = proveedor(3, SKU, 990.00, 3);
    private final Proveedor norte = proveedor(4, SKU, 980.00, 5);
    private final List<Proveedor> candidatos = List.of(sur, express, central, norte);

    private static Proveedor proveedor(long id, String sku, double precio, int leadTimeDias) {
        Proveedor proveedor = new Proveedor(id, "Proveedor " + id, "549110000000" + id);
        proveedor.getOfertas().add(new OfertaProveedor(sku, precio, leadTimeDias));
        return proveedor;
    }

    private static List<IEstrategiaProveedor> lasTres() {
        return List.of(new EstrategiaPrecio(), new EstrategiaVelocidad(), new EstrategiaBalanceada());
    }

    @Test
    void mismoProductoTresEstrategiasTresProveedores() {
        assertSame(sur, new EstrategiaPrecio().seleccionar(SKU, candidatos));
        assertSame(express, new EstrategiaVelocidad().seleccionar(SKU, candidatos));
        assertSame(central, new EstrategiaBalanceada().seleccionar(SKU, candidatos));
    }

    @Test
    void puntajesBalanceadosDeCervezaX() {
        // Precio normalizado: (p - 900) / 250. Lead time normalizado: (L - 1) / 5.
        EstrategiaBalanceada balanceada = new EstrategiaBalanceada();
        List<OfertaProveedor> ofertas = candidatos.stream().map(p -> p.getOferta(SKU)).toList();

        assertEquals(0.400, balanceada.puntaje(sur.getOferta(SKU), ofertas), 1e-9);     // 0.6·0    + 0.4·1
        assertEquals(0.600, balanceada.puntaje(express.getOferta(SKU), ofertas), 1e-9); // 0.6·1    + 0.4·0
        assertEquals(0.376, balanceada.puntaje(central.getOferta(SKU), ofertas), 1e-9); // 0.6·0.36 + 0.4·0.4
        assertEquals(0.512, balanceada.puntaje(norte.getOferta(SKU), ofertas), 1e-9);   // 0.6·0.32 + 0.4·0.8
    }

    @Test
    void elPrecioPesaMasQueLaVelocidad() {
        // Cada uno es el mejor en un criterio y el peor en el otro: gana el más barato (0.4 contra 0.6).
        Proveedor barato = proveedor(1, SKU, 100.00, 10);
        Proveedor rapido = proveedor(2, SKU, 200.00, 1);

        assertSame(barato, new EstrategiaBalanceada().seleccionar(SKU, List.of(rapido, barato)));
    }

    @Test
    void conPreciosIgualesLaBalanceadaDecidePorVelocidad() {
        Proveedor lento = proveedor(1, SKU, 500.00, 4);
        Proveedor rapido = proveedor(2, SKU, 500.00, 2);

        assertSame(rapido, new EstrategiaBalanceada().seleccionar(SKU, List.of(lento, rapido)));
    }

    @Test
    void empateDePrecioLoDesempataLaVelocidad() {
        Proveedor lento = proveedor(1, SKU, 900.00, 6);
        Proveedor rapido = proveedor(2, SKU, 900.00, 2);

        assertSame(rapido, new EstrategiaPrecio().seleccionar(SKU, List.of(lento, rapido)));
    }

    @Test
    void empateDeVelocidadLoDesempataElPrecio() {
        Proveedor caro = proveedor(1, SKU, 1200.00, 1);
        Proveedor barato = proveedor(2, SKU, 1100.00, 1);

        assertSame(barato, new EstrategiaVelocidad().seleccionar(SKU, List.of(caro, barato)));
    }

    @Test
    void unSoloCandidatoEsElElegidoConPuntajeCero() {
        EstrategiaBalanceada balanceada = new EstrategiaBalanceada();

        for (IEstrategiaProveedor estrategia : lasTres()) {
            assertSame(norte, estrategia.seleccionar(SKU, List.of(norte)));
        }
        assertEquals(0.0, balanceada.puntaje(norte.getOferta(SKU), List.of(norte.getOferta(SKU))), 1e-9);
    }

    @Test
    void seIgnoranLosCandidatosQueNoOfrecenElProducto() {
        // El más barato y el más rápido de todos, pero de otro producto
        Proveedor deAgua = proveedor(5, "AGUA-SG", 1.00, 1);
        List<Proveedor> mezclados = List.of(deAgua, sur, express, central, norte);

        assertSame(sur, new EstrategiaPrecio().seleccionar(SKU, mezclados));
        assertSame(express, new EstrategiaVelocidad().seleccionar(SKU, mezclados));
        assertSame(central, new EstrategiaBalanceada().seleccionar(SKU, mezclados));
    }

    @Test
    void sinProveedoresDelProductoFalla() {
        for (IEstrategiaProveedor estrategia : lasTres()) {
            assertThrows(NoSuchElementException.class, () -> estrategia.seleccionar("NO-EXISTE", candidatos));
            assertThrows(NoSuchElementException.class, () -> estrategia.seleccionar(SKU, List.of()));
        }
    }
}
