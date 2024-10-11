package com.nhom7.ecommercebackend.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@OpenAPIDefinition(
        info = @Info(
                title = "Đồ án E-commerce api in Java Spring boot",
                version = "1.0.0",
                description = "Ứng dụng bán điện thoại, Laptop, Đồng hồ, ..."
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local Development Server")
//                @Server(url = "http://45.117.179.16:8088", description = "Production Server"),
        }
)

@SecurityScheme(
        name = "bearer-key", // Can be any name, used to reference this scheme in the @SecurityRequirement annotation
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
@Configuration
public class SwaggerConfig {
//        @Value("${api.prefix}")
//        private static String API_PREFIX;
//        @Bean
//        public Docket api() {
//                return new Docket(DocumentationType.SWAGGER_2)
//                        .select()
//                        .apis(RequestHandlerSelectors.any())
//                        .paths(PathSelectors.ant("/api/v1/**"))
//                        .build();
//        }
}
