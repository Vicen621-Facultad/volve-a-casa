package io.github.grupo01.volve_a_casa.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Volve a Casa API")
                        .version("1.0.0")
                        .description("API REST para el sistema de mascotas perdidas - Proyecto TTPS Java")
                        .contact(new Contact()
                                .name("Grupo 01")
                                .email("grupo01@github.io"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Servidor de desarrollo"),
                        new Server().url("http://localhost:8080/volve-a-casa")
                                .description("Servidor con context path")));
    }
}
