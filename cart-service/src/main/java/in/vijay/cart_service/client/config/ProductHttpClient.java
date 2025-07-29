package in.vijay.cart_service.client.config;


import in.vijay.dto.ApiResponse;
import in.vijay.dto.product.ProductResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface ProductHttpClient {

    @GetExchange("/{id}")
    ApiResponse<ProductResponse> getProductById(@PathVariable String id);
}
