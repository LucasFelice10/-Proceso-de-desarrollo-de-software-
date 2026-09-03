
public class Main {
    public static void main(String[] args) {
        Carrito carrito = new Carrito();
        System.out.println("Estado inicial: " + carrito.getEstadoActual());
        System.out.println();

        carrito.pagar();
        System.out.println();

        
        carrito.agregarProducto("Zapatillas");
        System.out.println("Estado actual: " + carrito.getEstadoActual());
        System.out.println();

        
        carrito.agregarProducto("Medias");
        System.out.println("Estado actual: " + carrito.getEstadoActual());
        System.out.println();

        
        carrito.pagar();
        System.out.println("Estado actual: " + carrito.getEstadoActual());
        System.out.println();

        
        carrito.agregarProducto("Gorra");
        System.out.println();

        
        carrito.vaciar();
        System.out.println("Estado actual: " + carrito.getEstadoActual());
    }
}
