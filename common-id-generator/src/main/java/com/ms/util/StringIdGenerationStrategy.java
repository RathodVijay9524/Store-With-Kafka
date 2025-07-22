package com.ms.util;

import com.ms.service.IdGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("stringIdStrategy")
@RequiredArgsConstructor
public class StringIdGenerationStrategy implements IdGenerationStrategy<String> {
    private final IdGeneratorService idGenerator;

    @Override
    public String generateId(String entityName) {
        // Determine which format to use
        if (shouldUseDateBasedFormat(entityName)) {
            return idGenerator.generateDateBasedId(entityName, entityName.substring(0, 3));
        } else {
            return idGenerator.generateId(entityName, entityName.substring(0, 3), 8);
        }
    }

    private boolean shouldUseDateBasedFormat(String entityName) {
        // Configure which entities use date-based IDs
        return entityName.equalsIgnoreCase("PRODUCT")
                || entityName.equalsIgnoreCase("ORDER");
    }
}