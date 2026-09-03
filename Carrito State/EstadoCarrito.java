/**
 * Interfaz State.
 * Declara las operaciones que cada estado concreto del carrito
 * debe implementar, y que el Carrito (contexto) delega en el
 * estado actual.
 */
public interface EstadoCarrito {

    // Agregar un producto al carrito
    void agregarProducto(Carrito carrito, String producto);

    // Confirmar el pago del carrito
    void pagar(Carrito carrito);

    // Vaciar / cancelar el carrito
    void vaciar(Carrito carrito);

    // Nombre del estado, útil para mostrar información
    String getNombre();
}
