package com.vijay.ms;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.vijay.ms.entity.Inventory;
import com.vijay.ms.repository.InventoryRepository;

@SpringBootApplication
public class InventryServiceApp {
	
	public static void main(String[] args) {
        SpringApplication.run(InventryServiceApp.class, args);
    }
	    @Bean
	    CommandLineRunner seedInventory(InventoryRepository repository) {
	        return args -> {
	            if (!repository.existsById("PROD-29072025-00000001")) {
	                repository.save(Inventory.builder()
	                        .productId("PROD-29072025-00000001")
	                        .availableQuantity(100)
	                        .build());
	            }

	            if (!repository.existsById("PROD-29072025-00000002")) {
	                repository.save(Inventory.builder()
	                        .productId("PROD-29072025-00000002")
	                        .availableQuantity(25)
	                        .build());
	            }
				if (!repository.existsById("PROD-29072025-00000003")) {
					repository.save(Inventory.builder()
							.productId("PROD-29072025-00000003")
							.availableQuantity(25)
							.build());
				}
				if (!repository.existsById("PROD-29072025-00000004")) {
					repository.save(Inventory.builder()
							.productId("PROD-29072025-00000004")
							.availableQuantity(25)
							.build());
				}
	        };
	    }
	}

