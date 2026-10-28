package com.nexus.patterns.chain;

public abstract class ManejadorVenta {

    private ManejadorVenta siguiente;

    public ManejadorVenta setSiguiente(ManejadorVenta siguiente) {
        this.siguiente = siguiente;
        return siguiente;
    }

    public abstract void procesar(ContextoVenta contexto);

    protected void pasarAlSiguiente(ContextoVenta contexto) {
        if (siguiente != null) {
            siguiente.procesar(contexto);
        }
    }
}
