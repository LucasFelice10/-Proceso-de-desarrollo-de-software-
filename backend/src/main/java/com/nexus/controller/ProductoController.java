package com.nexus.controller;

import com.nexus.dao.IProductoDAO;
import com.nexus.model.Producto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final IProductoDAO productoDAO;

    public ProductoController(IProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }

    @GetMapping
    public List<Producto> listar() {
        return productoDAO.listarTodos();
    }
}
