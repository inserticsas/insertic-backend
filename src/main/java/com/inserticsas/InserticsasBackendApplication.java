package com.inserticsas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * INSERTIC SAS - Backend API
 * Multi-service platform: Energy, Communications, Security, IT Infrastructure
 */
@SpringBootApplication
@EnableJpaAuditing
public class InserticsasBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(InserticsasBackendApplication.class, args);
	}

}
