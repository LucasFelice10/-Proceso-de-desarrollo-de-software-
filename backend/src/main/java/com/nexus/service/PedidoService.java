package com.nexus.service;

import com.nexus.dao.IMovimientoDAO;
import com.nexus.dao.IPedidoDAO;
import com.nexus.dao.IProductoDAO;
import com.nexus.dao.IProveedorDAO;
import com.nexus.model.Movimiento;
import com.nexus.model.PedidoReposicion;
import com.nexus.model.Producto;
import com.nexus.model.Proveedor;
import com.nexus.patterns.observer.InventarioObservable;
import com.nexus.patterns.state.EstadoPedido;
import com.nexus.patterns.state.Recibido;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Contexto del patrón State: mueve el pedido por su ciclo de vida. Al recibirlo suma el stock a
 * través del inventario, así los observadores se enteran y la alerta se apaga sola.
 */
public class PedidoService {

    private final IPedidoDAO pedidoDAO;
    private final IMovimientoDAO movimientoDAO;
    private final IProductoDAO productoDAO;
    private final IProveedorDAO proveedorDAO;
    private final InventarioObservable inventario;
    private final GeneradorPedidos generador;

    public PedidoService(IPedidoDAO pedidoDAO, IMovimientoDAO movimientoDAO, IProductoDAO productoDAO,
                         IProveedorDAO proveedorDAO, InventarioObservable inventario, GeneradorPedidos generador) {
        this.pedidoDAO = pedidoDAO;
        this.movimientoDAO = movimientoDAO;
        this.productoDAO = productoDAO;
        this.proveedorDAO = proveedorDAO;
        this.inventario = inventario;
        this.generador = generador;
    }

    /**
     * Pedidos con su estado al día: los ENVIADOS que pasaron el plazo se marcan DEMORADOS.
     */
    public synchronized List<PedidoReposicion> listar() {
        List<PedidoReposicion> pedidos = pedidoDAO.listarTodos();
        for (PedidoReposicion pedido : pedidos) {
            EstadoPedido actual = EstadoPedido.de(pedido.getEstado());
            if (actual.evaluar(pedido) != actual) {
                pedidoDAO.actualizar(pedido);
            }
        }
        return pedidos;
    }

    /**
     * One-Click: genera el pedido con la estrategia activa, lo pasa a ENVIADO y arma el mensaje al
     * proveedor. El envío se simula.
     *
     * @throws NoSuchElementException si el producto no existe o ningún proveedor lo ofrece
     * @throws IllegalStateException si no hace falta reponer
     */
    public synchronized EnvioOneClick oneClick(String sku) {
        PedidoReposicion pedido = generador.ejecutarConexionOneClick(sku);
        EstadoPedido.de(pedido.getEstado()).avanzar(pedido);
        pedidoDAO.actualizar(pedido);

        Producto producto = productoDAO.buscarPorSku(sku)
                .orElseThrow(() -> new NoSuchElementException("Producto inexistente: " + sku));
        Proveedor proveedor = proveedorDAO.buscarPorId(pedido.getProveedorId())
                .orElseThrow(() -> new NoSuchElementException("Proveedor inexistente: " + pedido.getProveedorId()));
        String mensaje = MensajeOneClick.armar(pedido, producto, proveedor);
        return new EnvioOneClick(pedido, proveedor.getNombre(), mensaje, MensajeOneClick.link(proveedor, mensaje));
    }

    /**
     * Pasa el pedido al estado siguiente. Si queda RECIBIDO suma la cantidad al stock del producto.
     *
     * @throws NoSuchElementException si el pedido no existe
     * @throws IllegalStateException si el pedido ya fue recibido
     */
    public synchronized PedidoReposicion avanzar(long id) {
        PedidoReposicion pedido = buscar(id);
        EstadoPedido siguiente = EstadoPedido.de(pedido.getEstado()).avanzar(pedido);
        if (siguiente instanceof Recibido) {
            recibir(pedido);
        }
        pedidoDAO.actualizar(pedido);
        return pedido;
    }

    private void recibir(PedidoReposicion pedido) {
        inventario.agregarStock(pedido.getSku(), pedido.getCantidad());
        movimientoDAO.guardar(new Movimiento(pedido.getSku(), Movimiento.Tipo.RECEPCION, pedido.getCantidad()));
    }

    private PedidoReposicion buscar(long id) {
        return pedidoDAO.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("Pedido inexistente: " + id));
    }
}
