package com.fleet.maintenance;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FleetMaintenanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FleetMaintenanceApplication.class, args);
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Fleet & Asset Maintenance Platform API")
                        .description("REST API for managing fleet assets, maintenance orders, technicians, and fault logs")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Engineering Team")
                                .url("https://github.com")
                        )
                );
    }
}
