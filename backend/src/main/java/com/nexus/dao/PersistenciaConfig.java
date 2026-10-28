package com.nexus.dao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Elige la fábrica según la propiedad nexus.persistencia (h2 o memoria) y publica sus DAOs
 * para que los servicios y controladores los reciban por constructor.
 */
@Configuration
public class PersistenciaConfig {

    @Bean
    public DAOFactory daoFactory(@Value("${nexus.persistencia:h2}") String motor, DataSource dataSource) {
        return switch (motor.toLowerCase()) {
            case "h2" -> new H2DAOFactory(dataSource);
            case "memoria" -> MemoriaDAOFactory.conDatosDemo();
            default -> throw new IllegalStateException(
                    "nexus.persistencia debe ser 'h2' o 'memoria', no '" + motor + "'");
        };
    }

    @Bean
    public IProductoDAO productoDAO(DAOFactory fabrica) {
        return fabrica.getProductoDAO();
    }

    @Bean
    public IProveedorDAO proveedorDAO(DAOFactory fabrica) {
        return fabrica.getProveedorDAO();
    }

    @Bean
    public IPedidoDAO pedidoDAO(DAOFactory fabrica) {
        return fabrica.getPedidoDAO();
    }

    @Bean
    public IMovimientoDAO movimientoDAO(DAOFactory fabrica) {
        return fabrica.getMovimientoDAO();
    }
}
