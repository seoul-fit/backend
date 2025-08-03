package com.seoulfit.backend.shared.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "API Documentation", version = "1.0")
)
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        io.swagger.v3.oas.models.info.Info info = new io.swagger.v3.oas.models.info.Info()
                .title("Seoul-Fit Management API")
                .version("1.0")
                .description("API for managing users and their profile images.");

        return new OpenAPI()
                .info(info)
                .addSecurityItem(
                        new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components());
    }
}
