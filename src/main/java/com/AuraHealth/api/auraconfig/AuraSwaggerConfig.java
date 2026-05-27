package com.AuraHealth.api.auraconfig;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Pega el token JWT obtenido de POST /api/v1/auth/login"
)
public class AuraSwaggerConfig {

    @Bean
    public OpenAPI auraOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AuraHealth API")
                        .description("Plataforma de Salud Preventiva — Backend RESTful con Spring Boot")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo AuraHealth")
                                .email("aurahealth@upc.edu.pe")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new io.swagger.v3.oas.models.security.SecurityScheme()
                                        .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
