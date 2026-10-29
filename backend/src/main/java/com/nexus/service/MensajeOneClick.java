package com.nexus.service;

import com.nexus.model.PedidoReposicion;
import com.nexus.model.Producto;
import com.nexus.model.Proveedor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/** Arma el mensaje al proveedor con el SKU y la cantidad calculada, y su link tipo wa.me. */
public final class MensajeOneClick {

    private MensajeOneClick() {
    }

    public static String armar(PedidoReposicion pedido, Producto producto, Proveedor proveedor) {
        return "Hola " + proveedor.getNombre() + ", necesitamos reponer stock. "
                + "Pedido #" + pedido.getId() + ": SKU " + pedido.getSku() + " (" + producto.getNombre() + ")"
                + " - " + pedido.getCantidad() + " unidades. "
                + "Por favor confirmar disponibilidad y fecha de entrega. Gracias, Nexus Software.";
    }

    /** El envío real no existe: el link solo abre la conversación con el mensaje precargado. */
    public static String link(Proveedor proveedor, String mensaje) {
        String telefono = proveedor.getContacto() == null ? "" : proveedor.getContacto().replaceAll("\\D", "");
        String texto = URLEncoder.encode(mensaje, StandardCharsets.UTF_8).replace("+", "%20");
        return "https://wa.me/" + telefono + "?text=" + texto;
    }
}
