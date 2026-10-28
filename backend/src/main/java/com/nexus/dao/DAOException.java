package com.nexus.dao;

/** Error de acceso a datos; envuelve la excepción del motor para no filtrarla al negocio. */
public class DAOException extends RuntimeException {

    public DAOException(String mensaje) {
        super(mensaje);
    }

    public DAOException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
