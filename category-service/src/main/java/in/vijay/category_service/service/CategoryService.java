package in.vijay.category_service.service;

import com.ms.dto.category.CategoryRequestDTO;
import com.ms.dto.category.CategoryResponseDTO;

import java.util.List;

public interface CategoryService {
    CategoryResponseDTO createCategory(CategoryRequestDTO dto);
    CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO dto);
    void deleteCategory(Long id);
    List<CategoryResponseDTO> getAllCategories();
    CategoryResponseDTO getCategoryById(Long id);
}

