/**
 * Clase de prueba: ejemplifica el flujo secuencial de estados
 * Vacio -> ConProductos -> Pagado -> Vacio (nueva compra)
 */
public class Main {
    public static void main(String[] args) {
        Carrito carrito = new Carrito();
        System.out.println("Estado inicial: " + carrito.getEstadoActual());
        System.out.println();

        // Intento inválido: pagar un carrito vacío
        carrito.pagar();
        System.out.println();

        // Vacio -> ConProductos
        carrito.agregarProducto("Zapatillas");
        System.out.println("Estado actual: " + carrito.getEstadoActual());
        System.out.println();

        // Se mantiene en ConProductos
        carrito.agregarProducto("Medias");
        System.out.println("Estado actual: " + carrito.getEstadoActual());
        System.out.println();

        // ConProductos -> Pagado
        carrito.pagar();
        System.out.println("Estado actual: " + carrito.getEstadoActual());
        System.out.println();

        // Intento inválido: agregar producto luego de pagar
        carrito.agregarProducto("Gorra");
        System.out.println();

        // Pagado -> Vacio (nueva compra)
        carrito.vaciar();
        System.out.println("Estado actual: " + carrito.getEstadoActual());
    }
}
