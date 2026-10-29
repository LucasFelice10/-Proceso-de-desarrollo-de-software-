package com.nexus.controller;

import com.nexus.model.PedidoReposicion;
import com.nexus.service.EnvioOneClick;
import com.nexus.service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public List<PedidoReposicion> listar() {
        return pedidoService.listar();
    }

    @PostMapping("/one-click/{sku}")
    @ResponseStatus(HttpStatus.CREATED)
    public EnvioOneClick oneClick(@PathVariable String sku) {
        return pedidoService.oneClick(sku);
    }

    @PostMapping("/{id}/avanzar")
    public PedidoReposicion avanzar(@PathVariable long id) {
        return pedidoService.avanzar(id);
    }

    /** Solo para la demo: deja el pedido DEMORADO sin esperar los dos días reales. */
    @PostMapping("/{id}/simular-demora")
    public PedidoReposicion simularDemora(@PathVariable long id) {
        return pedidoService.simularDemora(id);
    }
}
