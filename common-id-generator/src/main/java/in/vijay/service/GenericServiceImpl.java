package in.vijay.service;


import in.vijay.mapper.GenericMapperInterface;
import in.vijay.model.BaseEntity;
import in.vijay.repository.GenericRepository;
import in.vijay.util.IdGenerationStrategy;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.List;


@RequiredArgsConstructor
public class GenericServiceImpl<T extends BaseEntity<ID> & Identifiable<ID>, Req, Res, ID> implements GenericServiceInterface<Req, Res, ID> {
    protected final GenericRepository<T, ID> repository;
    protected final GenericMapperInterface<T, Req, Res> mapper;
    private final IdGenerationStrategy<ID> idGenerationStrategy;

    @Override
    public Res create(Req request) {
        T entity = mapper.toEntity(request);
        String entityName = entity.getClass().getSimpleName().toUpperCase();
        entity.setId(idGenerationStrategy.generateId(entityName));
        return mapper.toResponseDto(repository.save(entity));
    }
    /**
     * Hook method to assign ID using IdAssignmentUtil
     */
   /* protected void assignIdIfNeeded(T entity) {
        // You can choose one of the two depending on your need
        //idAssignmentUtil.assignIdIfAbsent(entity, "ENTITY_KEY", "PREFIX-", 5);
        // or if you want date-based ID:
        idGenerator.generateId("PRODUCT", "PROD-", 5);
         //idAssignmentUtil.assignDateBasedIdIfAbsent(entity, "ORDER", "ORD");
    }*/

    @Override
    public Res findById(ID id) {
        T entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity with ID " + id + " not found"));
        return mapper.toResponseDto(entity);
    }

    @Override
    public List<Res> findAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    @Override
    public Res update(ID id, Req request) {
        getByIdOrThrow(id);
        T entity = mapper.toEntity(request);
        entity.setId(id);
        return mapper.toResponseDto(repository.save(entity));
    }

    @Override
    public void delete(ID id) {
        T entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity with ID " + id + " not found"));
        repository.delete(entity);
    }

    @Override
    public Res getByIdOrThrow(ID id) {
        T entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity with ID " + id + " not found"));
        return mapper.toResponseDto(entity);
    }


}
