package dev.faisal.portfolio.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI portfolioOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:" + serverPort);
        devServer.setDescription("Development server");

        Contact contact = new Contact();
        contact.setName("Mohamad Faisal");
        contact.setEmail("faisal@example.com");

        Info info = new Info()
                .title("Portfolio API")
                .version("1.0.0")
                .description("REST API for Faisal's Portfolio Platform")
                .contact(contact);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}
