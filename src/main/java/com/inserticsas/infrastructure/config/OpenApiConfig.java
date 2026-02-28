package com.inserticsas.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenApiConfig - Configuración de OpenAPI/Swagger
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.url:http://localhost:8080}")
    private String serverUrl;

    @Bean
    public OpenAPI inserticsasOpenAPI() {
        // Server local
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Servidor de Desarrollo");

        // Server producción
        Server prodServer = new Server();
        prodServer.setUrl(serverUrl);
        prodServer.setDescription("Servidor de Producción");

        // Información de contacto
        Contact contact = new Contact();
        contact.setEmail("info@inserticsas.com");
        contact.setName("INSERTIC SAS");
        contact.setUrl("https://inserticsas.com");

        // Licencia
        License license = new License()
                .name("Propietario")
                .url("https://inserticsas.com");

        // Información de la API
        Info info = new Info()
                .title("INSERTIC SAS - Backend API")
                .version("1.0.0")
                .contact(contact)
                .description("API REST para plataforma multi-servicio de INSERTIC SAS. " +
                        "Gestión de leads, calculadoras de riesgo, cotizaciones y más.")
                .termsOfService("https://inserticsas.com/terms")
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer, prodServer));
    }
}
