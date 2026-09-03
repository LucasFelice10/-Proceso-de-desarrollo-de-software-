/**
 * Estado concreto: el carrito no tiene productos.
 * Desde aquí solo es válido agregar un producto,
 * lo que provoca la transición a ConProductos.
 */
public class Vacio implements EstadoCarrito {

    @Override
    public void agregarProducto(Carrito carrito, String producto) {
        carrito.getProductos().add(producto);
        System.out.println("Se agregó \"" + producto + "\" al carrito.");
        System.out.println("Transición: Vacio -> ConProductos");
        carrito.setEstado(new ConProductos());
    }

    @Override
    public void pagar(Carrito carrito) {
        System.out.println("No se puede pagar: el carrito está vacío.");
    }

    @Override
    public void vaciar(Carrito carrito) {
        System.out.println("El carrito ya está vacío.");
    }

    @Override
    public String getNombre() {
        return "Vacío";
    }
}
