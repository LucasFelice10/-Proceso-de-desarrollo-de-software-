package com.nexus.dao;

/** Corre el contrato sobre la fábrica en memoria; si falla y H2 no, DatosDemo se desfasó de data.sql. */
class MemoriaDAOFactoryTest extends DAOFactoryContratoTest {

    @Override
    protected DAOFactory crearFabrica() {
        return MemoriaDAOFactory.conDatosDemo();
    }
}
