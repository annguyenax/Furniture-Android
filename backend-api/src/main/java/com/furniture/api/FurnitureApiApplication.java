package com.furniture.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main Spring Boot Application class for Furniture API
 *
 * @author Furniture Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
public class FurnitureApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FurnitureApiApplication.class, args);
        System.out.println("\n===========================================");
        System.out.println("Furniture API is running!");
        System.out.println("API Base URL: http://localhost:8080/api");
        System.out.println("===========================================\n");
    }
}
