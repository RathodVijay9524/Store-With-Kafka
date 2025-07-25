package in.vijay.repository;

import in.vijay.beans.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepository extends GenericRepository<Product,String> {
    List<Product> findByNameContainingIgnoreCase(String keyword);
    //List<Product> findByCategoryId(String categoryId);
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
}
