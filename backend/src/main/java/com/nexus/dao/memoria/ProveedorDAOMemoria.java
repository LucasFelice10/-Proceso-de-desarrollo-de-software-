package com.nexus.dao.memoria;

import com.nexus.dao.IProveedorDAO;
import com.nexus.model.OfertaProveedor;
import com.nexus.model.Proveedor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

public class ProveedorDAOMemoria implements IProveedorDAO {

    private final Map<Long, Proveedor> proveedores = new ConcurrentSkipListMap<>();
    private final AtomicLong secuencia = new AtomicLong();

    @Override
    public Optional<Proveedor> buscarPorId(long id) {
        return Optional.ofNullable(proveedores.get(id)).map(ProveedorDAOMemoria::copiar);
    }

    @Override
    public List<Proveedor> listarTodos() {
        return proveedores.values().stream().map(ProveedorDAOMemoria::copiar).toList();
    }

    @Override
    public List<Proveedor> listarPorProducto(String sku) {
        return proveedores.values().stream()
                .filter(p -> p.getOferta(sku) != null)
                .map(ProveedorDAOMemoria::copiar)
                .toList();
    }

    /** Le asigna el id siguiente, igual que la columna AUTO_INCREMENT de H2. */
    @Override
    public void guardar(Proveedor proveedor) {
        proveedor.setId(secuencia.incrementAndGet());
        proveedores.put(proveedor.getId(), copiar(proveedor));
    }

    private static Proveedor copiar(Proveedor p) {
        Proveedor copia = new Proveedor(p.getId(), p.getNombre(), p.getContacto());
        for (OfertaProveedor o : p.getOfertas()) {
            copia.getOfertas().add(new OfertaProveedor(o.getSku(), o.getPrecio(), o.getLeadTimeDias()));
        }
        return copia;
    }
}
