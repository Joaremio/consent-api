package br.com.sensedia.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sensedia Consent API")
                        .version("1.0.0")
                        .description("API para gerenciamento de consentimentos desenvolvida para o desafio técnico.")
                        .contact(new Contact()
                                .name("Joaremio Neto")
                                .url("https://github.com/Joaremio")));
    }
}