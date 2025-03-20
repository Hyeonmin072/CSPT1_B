package com.myong.backend.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("헤어리즘 API 문서")
                        .version("1.0")
                        .description("Swagger로 만든 API 문서"))
                .openapi("3.0.1");  // ✅ OpenAPI 버전 명확히 지정
    }
}
