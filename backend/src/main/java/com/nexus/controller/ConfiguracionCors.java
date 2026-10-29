package com.nexus.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Deja que el front de Vite (otro origen) llame a la API y abra el stream SSE. Los orígenes se
 * pueden cambiar con nexus.cors.origenes, separados por coma.
 */
@Configuration
public class ConfiguracionCors implements WebMvcConfigurer {

    private final String[] origenes;

    public ConfiguracionCors(
            @Value("${nexus.cors.origenes:http://localhost:5173,http://127.0.0.1:5173}") String[] origenes) {
        this.origenes = origenes;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(origenes)
                .allowedMethods("GET", "POST", "PUT", "OPTIONS")
                .allowedHeaders("*");
    }
}
