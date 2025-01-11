/* (C)2025 */
package com.diary.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {
    // API 정보 설정
    Info info =
        new Info()
            .title("Spring Template API")
            .description("Spring Template Application API Documentation")
            .version("v1.0.0")
            .license(new License().name("Apache 2.0").url("http://springdoc.org"));

    // JWT 인증 컴포넌트 설정
    SecurityScheme securityScheme =
        new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .in(SecurityScheme.In.HEADER)
            .name("Authorization");

    // Security 요구사항 설정
    SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

    return new OpenAPI()
        .info(info)
        .addSecurityItem(securityRequirement)
        .components(new Components().addSecuritySchemes("bearerAuth", securityScheme));
  }
}
