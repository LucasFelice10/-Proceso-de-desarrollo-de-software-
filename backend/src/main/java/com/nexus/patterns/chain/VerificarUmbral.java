package com.nexus.patterns.chain;

import com.nexus.model.Producto;

public class VerificarUmbral extends ManejadorVenta {

    @Override
    public void procesar(ContextoVenta contexto) {
        Producto producto = contexto.getProducto();
        boolean estabaSobre = contexto.getStockAnterior() > producto.getUmbralCritico();
        boolean quedaBajo = producto.getStockActual() <= producto.getUmbralCritico();
        contexto.setUmbralCruzado(estabaSobre && quedaBajo);
        pasarAlSiguiente(contexto);
    }
}
