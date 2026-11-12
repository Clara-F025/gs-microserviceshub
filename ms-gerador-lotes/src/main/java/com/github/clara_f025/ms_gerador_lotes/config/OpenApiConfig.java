package com.github.clara_f025.ms_gerador_lotes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("ms-gerador-lotes")
                .version("v1")
                .description("Domínio de origem: geradores (pontos geradores) e lotes de recicláveis - ColetaAgendada"));
    }
}
