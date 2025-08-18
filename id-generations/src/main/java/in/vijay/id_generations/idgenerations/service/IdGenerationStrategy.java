package in.vijay.id_generations.idgenerations.service;

public interface IdGenerationStrategy<ID> {
    ID generateId(String entityName);
}
