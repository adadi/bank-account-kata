package com.kata.bankaccount.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Central OpenAPI/Swagger configuration for the HTTP API docs.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Bank Account API",
                version = "v1",
                description = "API for deposits, withdrawals and account statements.",
                contact = @Contact(name = "Bank Account Team"),
                license = @License(name = "Unlicensed")
        ),
        servers = {
                @Server(url = "/", description = "Default server")
        }
)
public class OpenApiConfig {
}
