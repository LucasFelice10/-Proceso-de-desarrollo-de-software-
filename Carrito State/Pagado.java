
public class Pagado implements EstadoCarrito {

    @Override
    public void agregarProducto(Carrito carrito, String producto) {
        System.out.println("No se puede agregar \"" + producto + "\": el carrito ya fue pagado.");
    }

    @Override
    public void pagar(Carrito carrito) {
        System.out.println("El carrito ya fue pagado. No se puede pagar de nuevo.");
    }

    @Override
    public void vaciar(Carrito carrito) {
        carrito.getProductos().clear();
        System.out.println("Se vació el carrito luego del pago (nueva compra).");
        System.out.println("Transición: Pagado -> Vacio");
        carrito.setEstado(new Vacio());
    }

    @Override
    public String getNombre() {
        return "Pagado";
    }
}
