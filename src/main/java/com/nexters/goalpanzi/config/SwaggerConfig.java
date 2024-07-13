package com.nexters.goalpanzi.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .addServersItem(new Server().url("/"))
                .info(getMissionMateServerInfo());
    }

    private Info getMissionMateServerInfo() {
        return new Info().title("MissionMate Server API")
                .description("MissionMate Server API 명세서입니다.")
                .version("1.0.0");
    }
}
