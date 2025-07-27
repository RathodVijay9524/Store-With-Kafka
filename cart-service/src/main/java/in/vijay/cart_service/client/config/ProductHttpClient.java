package in.vijay.cart_service.client.config;


import in.vijay.dto.product.ProductResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(url = "http://localhost:8087/api/products")
public interface ProductHttpClient {
    @GetExchange("/{id}")
    ProductResponse getProductById(@PathVariable String id);
}
