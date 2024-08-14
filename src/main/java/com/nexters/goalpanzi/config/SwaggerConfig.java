package com.nexters.goalpanzi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String AUTHORIZATION = "Authorization";

        Components components = new Components().addSecuritySchemes(
                AUTHORIZATION,
                new SecurityScheme()
                        .name(AUTHORIZATION)
                        .in(SecurityScheme.In.HEADER)
                        .type(SecurityScheme.Type.APIKEY)
                        .description("Bearer ${ACCESS_TOKEN}")
        );
        return new OpenAPI()
                .components(components)
                .addServersItem(new Server().url("https://mission-mate.kro.kr"))
                .addServersItem(new Server().url("http://localhost:8080"))
                .info(getMissionMateServerInfo());
    }

    private Info getMissionMateServerInfo() {
        return new Info().title("MissionMate Server API")
                .description("MissionMate Server API 명세서입니다.")
                .version("1.0.0");
    }
}
