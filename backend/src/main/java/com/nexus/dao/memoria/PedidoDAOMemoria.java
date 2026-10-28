package com.nexus.dao.memoria;

import com.nexus.dao.DAOException;
import com.nexus.dao.IPedidoDAO;
import com.nexus.model.PedidoReposicion;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

public class PedidoDAOMemoria implements IPedidoDAO {

    private final Map<Long, PedidoReposicion> pedidos = new ConcurrentSkipListMap<>();
    private final AtomicLong secuencia = new AtomicLong();

    @Override
    public Optional<PedidoReposicion> buscarPorId(long id) {
        return Optional.ofNullable(pedidos.get(id)).map(PedidoDAOMemoria::copiar);
    }

    @Override
    public List<PedidoReposicion> listarTodos() {
        return pedidos.values().stream().map(PedidoDAOMemoria::copiar).toList();
    }

    @Override
    public List<PedidoReposicion> listarPorProducto(String sku) {
        return pedidos.values().stream()
                .filter(p -> p.getSku().equals(sku))
                .map(PedidoDAOMemoria::copiar)
                .toList();
    }

    @Override
    public List<PedidoReposicion> listarRecibidosPorProducto(String sku) {
        return pedidos.values().stream()
                .filter(p -> p.getSku().equals(sku) && "RECIBIDO".equals(p.getEstado()))
                .sorted(Comparator.comparing(PedidoReposicion::getFechaEnvio))
                .map(PedidoDAOMemoria::copiar)
                .toList();
    }

    @Override
    public void guardar(PedidoReposicion pedido) {
        pedido.setId(secuencia.incrementAndGet());
        pedidos.put(pedido.getId(), copiar(pedido));
    }

    @Override
    public void actualizar(PedidoReposicion pedido) {
        if (pedidos.replace(pedido.getId(), copiar(pedido)) == null) {
            throw new DAOException("Pedido inexistente: " + pedido.getId());
        }
    }

    private static PedidoReposicion copiar(PedidoReposicion p) {
        PedidoReposicion copia = new PedidoReposicion();
        copia.setId(p.getId());
        copia.setSku(p.getSku());
        copia.setProveedorId(p.getProveedorId());
        copia.setCantidad(p.getCantidad());
        copia.setEstado(p.getEstado());
        copia.setFechaCreacion(p.getFechaCreacion());
        copia.setFechaEnvio(p.getFechaEnvio());
        copia.setFechaRecepcion(p.getFechaRecepcion());
        return copia;
    }
}
