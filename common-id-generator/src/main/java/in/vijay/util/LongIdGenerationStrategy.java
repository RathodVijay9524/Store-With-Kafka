package in.vijay.util;

import in.vijay.service.IdGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("longIdStrategy")
@RequiredArgsConstructor
public class LongIdGenerationStrategy implements IdGenerationStrategy<Long> {
    private final IdGeneratorService idGenerator;

    @Override
    public Long generateId(String entityName) {
        return idGenerator.generateLongId(entityName);
    }

}

