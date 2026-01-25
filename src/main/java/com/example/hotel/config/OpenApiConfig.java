package com.example.hotel.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI hotelOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hotel Management API")
                        .description("RESTful APIs for Hotel Management System")
                        .version("v1"));
    }
}
