package com.nexus.patterns.observer;

import com.nexus.model.Producto;

public interface Observador {

    /** Se invoca cada vez que cambia el stock de un producto. */
    void actualizar(Producto producto);
}
