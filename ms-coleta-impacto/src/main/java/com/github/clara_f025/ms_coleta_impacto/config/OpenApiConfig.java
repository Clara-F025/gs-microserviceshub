package com.github.clara_f025.ms_coleta_impacto.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("ms-coleta-impacto")
                .version("v1")
                .description("Domínio de operação: coletores, coleta, validação de custódia por PIN e impacto ESG - ColetaAgendada"));
    }
}
