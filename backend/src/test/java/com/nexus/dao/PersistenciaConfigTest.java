package com.nexus.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class PersistenciaConfigTest {

    @Autowired
    private DAOFactory fabrica;

    @Autowired
    private IProductoDAO productoDAO;

    @Test
    void laAppArrancaConH2YLosDatosCargados() {
        assertInstanceOf(H2DAOFactory.class, fabrica);
        assertEquals(8, productoDAO.listarTodos().size());
    }

    @Test
    void laPropiedadEligeLaFabrica() {
        PersistenciaConfig config = new PersistenciaConfig();

        assertInstanceOf(MemoriaDAOFactory.class, config.daoFactory("memoria", null));
        assertInstanceOf(MemoriaDAOFactory.class, config.daoFactory("MEMORIA", null));
        assertThrows(IllegalStateException.class, () -> config.daoFactory("oracle", null));
    }
}
