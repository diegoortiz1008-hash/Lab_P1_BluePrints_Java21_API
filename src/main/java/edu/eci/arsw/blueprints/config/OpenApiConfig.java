package edu.eci.arsw.blueprints.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI blueprintsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Blueprints API")
                        .description("API REST para gestión de planos arquitectónicos - ARSW Lab")
                        .version("v1.0"));
    }
}