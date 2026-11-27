package com.senac.bibliotecaapi.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Cria o seletor v1 / v2 no Swagger UI.
// v1: toda a API (GET /livros/{id} completo, com HATEOAS).
// v2: apenas GET /livros/{id} resumido (cabeçalho X-API-Version: 2).
@Configuration
public class OpenApiGrupos {

    @Bean
    public GroupedOpenApi v1() {
        return GroupedOpenApi.builder()
                .group("v1")
                .pathsToMatch("/**")
                .addOpenApiMethodFilter(m -> !m.getName().equals("buscarV2"))
                .build();
    }

    @Bean
    public GroupedOpenApi v2() {
        return GroupedOpenApi.builder()
                .group("v2")
                .pathsToMatch("/livros/{id}")
                .addOpenApiMethodFilter(m -> m.getName().equals("buscarV2"))
                .build();
    }
}