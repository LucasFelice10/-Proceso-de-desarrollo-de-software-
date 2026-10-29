package com.nexus.service;

import com.nexus.model.PedidoReposicion;

/** Resultado del One-Click: el pedido ya enviado y el mensaje listo para el proveedor. */
public record EnvioOneClick(PedidoReposicion pedido, String proveedor, String mensaje, String link) {
}
