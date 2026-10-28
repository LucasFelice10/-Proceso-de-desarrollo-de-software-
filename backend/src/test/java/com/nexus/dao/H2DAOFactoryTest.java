package com.nexus.dao;

import org.h2.jdbcx.JdbcDataSource;

import java.sql.Connection;
import java.sql.Statement;

/** Corre el contrato sobre H2 real, recreando la base con schema.sql y data.sql antes de cada prueba. */
class H2DAOFactoryTest extends DAOFactoryContratoTest {

    @Override
    protected DAOFactory crearFabrica() throws Exception {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:contrato;DB_CLOSE_DELAY=-1");
        try (Connection con = dataSource.getConnection(); Statement st = con.createStatement()) {
            st.execute("RUNSCRIPT FROM 'classpath:schema.sql' CHARSET 'UTF-8'");
            st.execute("RUNSCRIPT FROM 'classpath:data.sql' CHARSET 'UTF-8'");
        }
        return new H2DAOFactory(dataSource);
    }
}
