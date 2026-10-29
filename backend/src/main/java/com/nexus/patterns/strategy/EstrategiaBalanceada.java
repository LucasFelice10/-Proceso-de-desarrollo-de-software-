package com.nexus.patterns.strategy;

import com.nexus.model.OfertaProveedor;
import com.nexus.model.Proveedor;

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Perfil Balanceado: puntúa cada oferta con 60% precio y 40% velocidad y elige el menor puntaje.
 * Precio y lead time se normalizan entre 0 (el mejor de los candidatos) y 1 (el peor), así se
 * pueden sumar aunque uno esté en pesos y el otro en días.
 */
public class EstrategiaBalanceada implements IEstrategiaProveedor {

    public static final double PESO_PRECIO = 0.6;
    public static final double PESO_VELOCIDAD = 0.4;

    @Override
    public Proveedor seleccionar(String sku, List<Proveedor> candidatos) {
        List<Proveedor> conOferta = candidatos.stream()
                .filter(p -> p.getOferta(sku) != null)
                .toList();
        List<OfertaProveedor> ofertas = conOferta.stream().map(p -> p.getOferta(sku)).toList();
        return conOferta.stream()
                .min(Comparator.comparingDouble(p -> puntaje(p.getOferta(sku), ofertas)))
                .orElseThrow(() -> new NoSuchElementException("Ningún proveedor ofrece " + sku));
    }

    /** Puntaje de una oferta frente al resto: 0 si es la más barata y la más rápida, 1 si es la peor en ambas. */
    public double puntaje(OfertaProveedor oferta, List<OfertaProveedor> ofertas) {
        DoubleSummaryStatistics precios = ofertas.stream()
                .mapToDouble(OfertaProveedor::getPrecio)
                .summaryStatistics();
        IntSummaryStatistics demoras = ofertas.stream()
                .mapToInt(OfertaProveedor::getLeadTimeDias)
                .summaryStatistics();
        return PESO_PRECIO * normalizar(oferta.getPrecio(), precios.getMin(), precios.getMax())
                + PESO_VELOCIDAD * normalizar(oferta.getLeadTimeDias(), demoras.getMin(), demoras.getMax());
    }

    /** Lleva el valor a [0, 1]; si todos los candidatos tienen el mismo valor, ese criterio no desempata. */
    private static double normalizar(double valor, double min, double max) {
        return max == min ? 0 : (valor - min) / (max - min);
    }
}
