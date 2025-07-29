package in.vijay.util;

import jakarta.transaction.Transactional;

public interface IdGenerationStrategy<ID> {
    ID generateId(String entityName);

}
