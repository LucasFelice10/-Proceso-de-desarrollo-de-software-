package com.nexus.controller;

import com.nexus.dao.IProveedorDAO;
import com.nexus.model.Proveedor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    private final IProveedorDAO proveedorDAO;

    public ProveedorController(IProveedorDAO proveedorDAO) {
        this.proveedorDAO = proveedorDAO;
    }

    @GetMapping
    public List<Proveedor> listar() {
        return proveedorDAO.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> obtener(@PathVariable long id) {
        return proveedorDAO.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
