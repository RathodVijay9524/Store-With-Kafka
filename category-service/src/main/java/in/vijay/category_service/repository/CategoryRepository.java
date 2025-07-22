package in.vijay.category_service.repository;

import in.vijay.category_service.beans.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByActiveTrue();
}

