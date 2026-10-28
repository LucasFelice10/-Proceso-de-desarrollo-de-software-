package com.nexus.patterns.chain;

public class DispararAlerta extends ManejadorVenta {

    @Override
    public void procesar(ContextoVenta contexto) {
        if (contexto.isUmbralCruzado()) {
            contexto.setAlertaDisparada(true);
        }
        pasarAlSiguiente(contexto);
    }
}
