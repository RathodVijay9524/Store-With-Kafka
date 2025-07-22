package com.ms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@Configuration
public class JpaConfig {

    @Bean
    public AuditorAware<Integer> auditorProvider() {
        // You can return the logged-in user here in real apps
        return () -> Optional.of(1);
    }
}