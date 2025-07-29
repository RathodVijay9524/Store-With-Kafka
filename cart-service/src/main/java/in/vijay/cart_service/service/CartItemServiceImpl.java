package in.vijay.cart_service.service;

import in.vijay.cart_service.beans.CartItem;
import in.vijay.cart_service.client.config.IdGeneratorClient;
import in.vijay.cart_service.client.config.ProductHttpClient;
import in.vijay.cart_service.repository.CartItemRepository;
import in.vijay.dto.ApiResponse;
import in.vijay.dto.product.ProductResponse;
import in.vijay.service.IdGeneratorService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import static in.vijay.cart_service.PrefixGenerator.generateSequential;

@Service
@AllArgsConstructor
public class CartItemServiceImpl implements CartItemService{
    private final CartItemRepository cartItemRepository;
    private final ProductHttpClient productHttpClient;
    private final IdGeneratorClient idGeneratorClient;
    @Override
    public CartItem createCartItem(String productId, int quantity) {
        // Fetch product from external service
        ApiResponse<ProductResponse> response = productHttpClient.getProductById(productId);
        ProductResponse product = response.getData(); // ✅ unwrap the actual product

        if (product == null) {
            throw new EntityNotFoundException("Product not found for ID: " + productId);
        }

        if (product.getPrice() == null) {
            throw new IllegalStateException("Product price is null for product ID: " + productId);
        }

        // ✅ Build CartItem
        CartItem cartItem = CartItem.builder()
                .productId(product.getId())
                .productName(product.getName())
                .price(product.getPrice()) // ✅ safe now
                .quantity(quantity)
                .build();

        cartItem.setId(idGeneratorClient.generateId("ITEMS", "ITM",6));
        return cartItemRepository.save(cartItem);
    }


    @Override
    public void removeCartItem(String cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found with ID: " + cartItemId));
        cartItemRepository.delete(cartItem);
    }
    @Override
    public CartItem findById(String id) {
        return cartItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found with ID: " + id));
    }
}
