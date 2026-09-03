import java.util.ArrayList;
import java.util.List;


public class Carrito {

    private EstadoCarrito estado;
    private final List<String> productos = new ArrayList<>();

    public Carrito() {
        this.estado = new Vacio();
    }

    public void agregarProducto(String producto) {
        estado.agregarProducto(this, producto);
    }

    public void pagar() {
        estado.pagar(this);
    }

    public void vaciar() {
        estado.vaciar(this);
    }

    public void setEstado(EstadoCarrito estado) {
        this.estado = estado;
    }

    public String getEstadoActual() {
        return estado.getNombre();
    }

    public List<String> getProductos() {
        return productos;
    }
}
