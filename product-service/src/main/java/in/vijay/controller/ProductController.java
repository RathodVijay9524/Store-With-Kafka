package in.vijay.controller;

import in.vijay.dto.PageableResponse;
import in.vijay.dto.product.ProductRequest;
import in.vijay.dto.product.ProductResponse;
import in.vijay.service.ProductService;
import in.vijay.util.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getProductsByCategoryId(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        PageableResponse<ProductResponse> products = productService.getAllProductsByCategoryId(
                categoryId, pageNumber, pageSize, sortBy, sortDir);
        return ExceptionUtil.createBuildResponse(products, HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductRequest requestDto) {
        ProductResponse responseDto = productService.createProduct(requestDto);
        return ExceptionUtil.createBuildResponse(responseDto, HttpStatus.CREATED);
    }

    // ✅ DELETE endpoint
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ExceptionUtil.createBuildResponse("Product deleted successfully!", HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable String id) {
        ProductResponse product = productService.findById(id);
        return ExceptionUtil.createBuildResponse(product, HttpStatus.OK);
    }

    // ✅ Paginated + Sorted List of Products
    @GetMapping
    public ResponseEntity<?> getAllProducts(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createdOn") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        PageableResponse<ProductResponse> response = productService.findAll(pageNumber, pageSize, sortBy, sortDir);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable String id, @RequestBody ProductRequest requestDto) {
        ProductResponse updated = productService.updateProduct(id, requestDto);
        return ExceptionUtil.createBuildResponse(updated, HttpStatus.OK);
    }


    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(@RequestParam String keyword) {
        List<ProductResponse> results = productService.searchByName(keyword);
        return ExceptionUtil.createBuildResponse(results, HttpStatus.OK);
    }
}
