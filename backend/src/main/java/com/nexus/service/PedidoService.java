package com.nexus.service;

import com.nexus.dao.IMovimientoDAO;
import com.nexus.dao.IPedidoDAO;
import com.nexus.model.Movimiento;
import com.nexus.model.PedidoReposicion;
import com.nexus.patterns.observer.InventarioObservable;
import com.nexus.patterns.state.EstadoPedido;
import com.nexus.patterns.state.Recibido;

import java.util.NoSuchElementException;

/**
 * Contexto del patrón State: mueve el pedido por su ciclo de vida. Al recibirlo suma el stock a
 * través del inventario, así los observadores se enteran y la alerta se apaga sola.
 */
public class PedidoService {

    private final IPedidoDAO pedidoDAO;
    private final IMovimientoDAO movimientoDAO;
    private final InventarioObservable inventario;

    public PedidoService(IPedidoDAO pedidoDAO, IMovimientoDAO movimientoDAO, InventarioObservable inventario) {
        this.pedidoDAO = pedidoDAO;
        this.movimientoDAO = movimientoDAO;
        this.inventario = inventario;
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
