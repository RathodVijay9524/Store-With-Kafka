package in.vijay.category_service.service;

import in.vijay.dto.category.CategoryRequestDTO;
import in.vijay.dto.category.CategoryResponseDTO;
import in.vijay.category_service.beans.Category;
import in.vijay.category_service.event.CategoryEventPublisher;
import in.vijay.category_service.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;
    private final ModelMapper mapper;
    private final CategoryEventPublisher publisher;

    @Override
    public CategoryResponseDTO createCategory(CategoryRequestDTO dto) {
        Category category = mapper.map(dto, Category.class);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        Category saved = repository.save(category);

        publisher.publishCategoryCreatedEvent(saved); // 🟢

        return mapper.map(saved, CategoryResponseDTO.class);
    }

    @Override
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO dto) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setImageUrl(dto.getImageUrl());
        category.setActive(dto.isActive());
        category.setUpdatedAt(LocalDateTime.now());
        Category updated = repository.save(category);

        publisher.publishCategoryUpdatedEvent(updated); // 🟢

        return mapper.map(updated, CategoryResponseDTO.class);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        repository.deleteById(id);

        publisher.publishCategoryDeletedEvent(category); // 🟢
    }

    @Override
    public List<CategoryResponseDTO> getAllCategories() {
        return repository.findAll().stream()
                .map(category -> mapper.map(category, CategoryResponseDTO.class))
                .collect(Collectors.toList());
    }


    @Override
    public CategoryResponseDTO getCategoryById(Long id) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        return mapper.map(category, CategoryResponseDTO.class);
    }



}
