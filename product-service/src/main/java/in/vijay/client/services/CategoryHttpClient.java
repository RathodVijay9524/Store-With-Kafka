package in.vijay.client.services;

import in.vijay.dto.category.CategoryResponseDTO;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.bind.annotation.PathVariable;

@HttpExchange(url = "http://localhost:8084/api/categories")
public interface CategoryHttpClient {

    @GetExchange("/{id}")
    public CategoryResponseDTO getCategoryById(@PathVariable Long id);
}

