package com.nexus.service;

import com.nexus.dao.IMovimientoDAO;
import com.nexus.dao.IPedidoDAO;
import com.nexus.dao.IProductoDAO;
import com.nexus.dao.IProveedorDAO;
import com.nexus.patterns.observer.InventarioObservable;
import com.nexus.patterns.observer.ObservadorSSE;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Publica los servicios y el observable como beans para que los controladores los reciban por
 * constructor. El inventario arranca con el observador SSE ya suscripto.
 */
@Configuration
public class ServiciosConfig {

    @Bean
    public ObservadorSSE observadorSSE() {
        return new ObservadorSSE();
    }

    @Bean
    public InventarioObservable inventarioObservable(IProductoDAO productoDAO, ObservadorSSE observadorSSE) {
        InventarioObservable inventario = new InventarioObservable(productoDAO);
        inventario.agregarObservador(observadorSSE);
        return inventario;
    }

    @Bean
    public VentaService ventaService(IProductoDAO productoDAO, InventarioObservable inventario) {
        return new VentaService(productoDAO, inventario);
    }

    @Bean
    public ReposicionService reposicionService(IProductoDAO productoDAO, IProveedorDAO proveedorDAO,
                                               IPedidoDAO pedidoDAO, IMovimientoDAO movimientoDAO) {
        return new ReposicionService(productoDAO, proveedorDAO, pedidoDAO, movimientoDAO);
    }

    @Bean
    public GeneradorPedidos generadorPedidos(IProveedorDAO proveedorDAO, IPedidoDAO pedidoDAO,
                                             ReposicionService reposicion) {
        return new GeneradorPedidos(proveedorDAO, pedidoDAO, reposicion);
    }

    @Bean
    public PedidoService pedidoService(IPedidoDAO pedidoDAO, IMovimientoDAO movimientoDAO,
                                       IProductoDAO productoDAO, IProveedorDAO proveedorDAO,
                                       InventarioObservable inventario, GeneradorPedidos generador) {
        return new PedidoService(pedidoDAO, movimientoDAO, productoDAO, proveedorDAO, inventario, generador);
    }
}
