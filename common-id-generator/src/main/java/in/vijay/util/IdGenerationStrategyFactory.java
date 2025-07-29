package in.vijay.util;

import org.springframework.stereotype.Component;

@Component
public class IdGenerationStrategyFactory {

    private final StringIdGenerationStrategy stringIdStrategy;
    private final LongIdGenerationStrategy longIdStrategy;

    public IdGenerationStrategyFactory(StringIdGenerationStrategy stringIdStrategy,
                                       LongIdGenerationStrategy longIdStrategy) {
        this.stringIdStrategy = stringIdStrategy;
        this.longIdStrategy = longIdStrategy;
    }

    @SuppressWarnings("unchecked")
    public <ID> IdGenerationStrategy<ID> getStrategy(Class<ID> idType) {
        if (idType.equals(String.class)) {
            return (IdGenerationStrategy<ID>) stringIdStrategy;
        } else if (idType.equals(Long.class)) {
            return (IdGenerationStrategy<ID>) longIdStrategy;
        }
        throw new IllegalArgumentException("Unsupported ID type: " + idType.getName());
    }
}

/*
@Component
@RequiredArgsConstructor
public class IdGenerationStrategyFactory {
    private final StringIdGenerationStrategy stringIdStrategy;
    private final LongIdGenerationStrategy longIdStrategy;

    @SuppressWarnings("unchecked")
    public <ID> IdGenerationStrategy<ID> getStrategy(Class<ID> idType) {
        if (idType.equals(String.class)) {
            return (IdGenerationStrategy<ID>) stringIdStrategy;
        } else if (idType.equals(Long.class)) {
            return (IdGenerationStrategy<ID>) longIdStrategy;
        }
        throw new IllegalArgumentException("Unsupported ID type: " + idType.getName());
    }
}*/
