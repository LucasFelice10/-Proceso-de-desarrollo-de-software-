
public class ConProductos implements EstadoCarrito {

    @Override
    public void agregarProducto(Carrito carrito, String producto) {
        carrito.getProductos().add(producto);
        System.out.println("Se agregó \"" + producto + "\" al carrito.");
        
    }

    @Override
    public void pagar(Carrito carrito) {
        System.out.println("Procesando pago de " + carrito.getProductos().size() + " producto(s)...");
        System.out.println("Transición: ConProductos -> Pagado");
        carrito.setEstado(new Pagado());
    }

    @Override
    public void vaciar(Carrito carrito) {
        carrito.getProductos().clear();
        System.out.println("Se vació el carrito.");
        System.out.println("Transición: ConProductos -> Vacio");
        carrito.setEstado(new Vacio());
    }

    @Override
    public String getNombre() {
        return "Con Productos";
    }
}
