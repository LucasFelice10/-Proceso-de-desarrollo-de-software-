
public interface EstadoCarrito {

    
    void agregarProducto(Carrito carrito, String producto);

    
    void pagar(Carrito carrito);

    
    void vaciar(Carrito carrito);

    
    String getNombre();
}
