package in.vijay.service;

import in.vijay.dto.PageableResponse;
import in.vijay.dto.product.ProductRequest;
import in.vijay.dto.product.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductRequest productRequest);
    public void deleteProduct(String id);
    ProductResponse findById(String id);
    ProductResponse updateProduct(String id,ProductRequest productRequest);
    List<ProductResponse> searchByName(String name);
    PageableResponse<ProductResponse> getAllProductsByCategoryId(Long categoryId, int pageNumber, int pageSize, String sortBy, String sortDir);
    PageableResponse<ProductResponse> findAll(int pageNumber, int pageSize, String sortBy, String sortDir);
}
