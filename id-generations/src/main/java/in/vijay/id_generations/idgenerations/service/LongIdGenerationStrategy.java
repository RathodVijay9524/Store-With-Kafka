package in.vijay.id_generations.idgenerations.service;

import in.vijay.id_generations.idgenerations.IdGeneratorServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("longIdStrategy")
@RequiredArgsConstructor
public class LongIdGenerationStrategy implements IdGenerationStrategy<Long> {
    private final IdGeneratorServices idGenerator;

    @Override
    public Long generateId(String entityName) {
        return idGenerator.generateLongId(entityName);
    }

}

