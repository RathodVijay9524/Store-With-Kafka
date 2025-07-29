package in.vijay.service;

import in.vijay.client.services.CategoryHttpClient;
import in.vijay.dto.PageableResponse;
import in.vijay.dto.category.CategoryResponseDTO;
import in.vijay.dto.product.ProductRequest;
import in.vijay.dto.product.ProductResponse;

import in.vijay.event.ProductEventPublisher;
import in.vijay.mapper.GenericMapperInterface;
import in.vijay.mapper.ProductMapper;
import in.vijay.repository.GenericRepository;
import in.vijay.util.Helper;
import in.vijay.util.IdGenerationStrategy;
import in.vijay.beans.Product;
import in.vijay.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProductServiceImpl extends GenericServiceImpl<Product, ProductRequest, ProductResponse, String>
        implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    //private final IdGenerationStrategy<String> idGenerationStrategy;
    private final IdGeneratorService idGenerator;
    private final CategoryHttpClient categoryHttpClient;
    private final ProductEventPublisher productEventPublisher;

    public ProductServiceImpl(GenericRepository<Product, String> repository,
                              GenericMapperInterface<Product, ProductRequest, ProductResponse> mapper,
                              @Qualifier("stringIdStrategy") IdGenerationStrategy<String> idGenerationStrategy,
                              ProductRepository productRepository, ProductMapper productMapper, IdGeneratorService idGenerator, CategoryHttpClient categoryHttpClient, ProductEventPublisher productEventPublisher) {
        super(repository, mapper, idGenerationStrategy);
        this.productRepository = productRepository;
        this.categoryHttpClient = categoryHttpClient;
        //this.idGenerationStrategy = idGenerationStrategy;
        this.productMapper = productMapper;
        this.idGenerator = idGenerator;
        this.productEventPublisher = productEventPublisher;
    }


    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        // Step 1: Validate Category
        log.info("🔁 Creating product with request: {}", productRequest);

        CategoryResponseDTO category = categoryHttpClient.getCategoryById(productRequest.getCategoryId());
        if (category == null) {
            log.error("❌ Category not found with ID: {}", productRequest.getCategoryId());
            throw new EntityNotFoundException("Category not found with ID: " + productRequest.getCategoryId());
        }
        Product product = productMapper.toEntity(productRequest);
        // Step 2: Generate ID using strategy
       // String entityName = Product.class.getSimpleName().toUpperCase(); // "PRODUCT"
        //String generatedId = idGenerationStrategy.generateId(entityName);
        //product.setId(idGenerationStrategy.generateId("PRODUCT"));
        //product.setId(generatedId);
        //product.setId(idGenerator.generateId("PRODUCT", "PROD", 8));

        // Step 3: Generate Product ID
        product.setId(idGenerator.generateDateBasedId("PRODUCT", "PROD"));

        // Fix: Set just the ID from the category DTO
        product.setCategoryId(category.getId());

        // Step 4: Save to DB
        Product savedProduct = productRepository.save(product);

        // Step 5: Publish Kafka event 🟢
        productEventPublisher.publishProductCreated(savedProduct);

        log.info("✅ Product created with ID: {}", savedProduct.getId());

        // Step 6: Map to response DTO
        return productMapper.toResponseDto(savedProduct);
    }

    @Override
    public ProductResponse findById(String id) {
        return super.findById(id); // Calls GenericServiceImpl.findById
    }

    @Override
    public ProductResponse updateProduct(String id, ProductRequest productRequest) {
        CategoryResponseDTO category = categoryHttpClient.getCategoryById(productRequest.getCategoryId());
        if (category == null) {
            throw new EntityNotFoundException("Category not found with ID: " + productRequest.getCategoryId());
        }
        // Convert request to entity and set required fields
        Product entity = productMapper.toEntity(productRequest);
        entity.setId(id);
        entity.setCategoryId(category.getId());

        log.info("🔁 Updating product with ID: {}", id);

        Product updated = productRepository.save(entity);

        // 🔁 Publish update event
        productEventPublisher.publishProductUpdated(updated);

        return productMapper.toResponseDto(updated);// Calls GenericServiceImpl.update
    }

    @Override
    public void deleteProduct(String id) {
        // Step 1: Find product
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + id));

        // Step 2: Delete the product
        productRepository.delete(product);

        log.warn("🗑 Deleting product with ID: {}", id);

        // Step 3: Publish delete event
        productEventPublisher.publishProductDeleted(product);
    }


    @Override
    public List<ProductResponse> searchByName(String name) {
        if(name==null||name.trim().isEmpty()){
            throw new IllegalArgumentException("Search keyword must not be empty");
        }
        log.info("🔍 Searching products by name: {}", name);
        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
        if(products.isEmpty()){
            throw new EntityNotFoundException("No Product found with name: " + name);
        }
        return productMapper.toResponseDtoList(products);
    }

    @Override
    public PageableResponse<ProductResponse> getAllProductsByCategoryId(Long categoryId, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> pageResult = productRepository.findByCategoryId(categoryId, pageable);
        return Helper.getPageableResponse(pageResult, mapper::toResponseDto);

    }

    @Override
    public PageableResponse<ProductResponse> findAll(int pageNumber, int pageSize, String sortBy, String sortDir) {
        log.info("📄 Fetching all products. Page: {}, Size: {}, SortBy: {}, SortDir: {}", pageNumber, pageSize, sortBy, sortDir);
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> pageResult = productRepository.findAll(pageable);
        return Helper.getPageableResponse(pageResult, mapper::toResponseDto);
    }
}
