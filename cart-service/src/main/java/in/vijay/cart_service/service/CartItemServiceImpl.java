package in.vijay.cart_service.service;

import in.vijay.cart_service.beans.CartItem;
import in.vijay.cart_service.client.config.ProductHttpClient;
import in.vijay.cart_service.repository.CartItemRepository;
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
    @Override
    public CartItem createCartItem(String productId, int quantity) {
        // Fetch product details from product-service via HTTP
        ProductResponse product = productHttpClient.getProductById(productId);
        if (product == null) {
            throw new EntityNotFoundException("Product not found for ID: " + productId);
        }
         // Build CartItem
        CartItem cartItem = CartItem.builder()
                .productId(product.getId())
                .productName(product.getName())
                .price(product.getPrice())
                .quantity(quantity)
                .build();
        cartItem.setId(generateSequential("ITEM"));
        // Save and return
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
