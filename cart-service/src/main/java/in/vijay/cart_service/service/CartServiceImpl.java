package in.vijay.cart_service.service;

import in.vijay.cart_service.beans.Cart;
import in.vijay.cart_service.beans.CartItem;
import in.vijay.cart_service.client.config.IdGeneratorClient;
import in.vijay.cart_service.client.config.UserHttpClient;
import in.vijay.cart_service.event.CartEventPublisher;
import in.vijay.cart_service.repository.CartRepository;
import in.vijay.dto.order.AddItemToCartRequest;
import in.vijay.dto.order.CartItemResponse;
import in.vijay.dto.order.CartResponse;
import in.vijay.dto.user.UserResponse;
import in.vijay.event.cart.CartClearedEvent;
import in.vijay.event.cart.CartCreatedEvent;
import in.vijay.event.cart.CartItemAddedEvent;
import in.vijay.exceptions.BadApiRequestException;
import in.vijay.service.IdGeneratorService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static in.vijay.cart_service.PrefixGenerator.generateSequential;

@Slf4j
@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService{

    private final CartRepository cartRepository;
    private final CartItemService cartItemService;
    private final UserHttpClient userHttpClient;
    private final CartEventPublisher cartEventPublisher;
    private final IdGeneratorClient idGeneratorClient;


    @Override
    public String createCart(String userId, AddItemToCartRequest request) {
        // 1. Validate Quantity
        if (request.getQuantity() <= 0) {
            throw new BadApiRequestException("Quantity must be greater than zero.");
        }

        // 2. Validate User
        UserResponse user = userHttpClient.getUserById(userId);
        if (user == null) {
            throw new EntityNotFoundException("User not found for ID: " + userId);
        }

        // 🔍 Use cartExistsByUserId to add condition/logging/metrics
        if (!cartExistsByUserId(user.getId())) {
            log.info("No cart exists for user {}, creating new cart", userId);
        } else {
            log.info("Cart already exists for user {}", userId);
        }

        boolean alreadyExists = cartExistsByUserId(user.getId());
        if (alreadyExists) {
            log.warn("Cart already exists for user ID: {}", user.getId());
        }


        // 3. Fetch or Create Cart
        Cart cart = cartRepository.findByUserId(user.getId()).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setId(idGeneratorClient.generateDateBasedId("CARTS", "CART"));
            newCart.setUserId(user.getId());
            newCart.setItems(new ArrayList<>());
            return newCart;
        });

        // 4. Check if product already exists in cart
        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            // ✅ Update existing item
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(request.getQuantity());
        } else {
            // ✅ Create new cart item and add to cart
            CartItem cartItem = cartItemService.createCartItem(request.getProductId(), request.getQuantity());
            cartItem.setCart(cart); // maintain bidirectional link
            cart.getItems().add(cartItem);

            CartItemAddedEvent itemAddedEvent = CartItemAddedEvent.builder()
                    .cartId(cart.getId())
                    .productId(cartItem.getProductId())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getPrice())
                    .build();
            cartEventPublisher.publishCartItemAdded(itemAddedEvent);
        }


        // 5. Recalculate total and save
        cart.recalculateTotalPrice();
        Cart saved = cartRepository.save(cart);

        // ✅ Publish event
        CartCreatedEvent event = CartCreatedEvent.builder()
                .cartId(saved.getId())
                .userId(saved.getUserId())
                .totalPrice(saved.getTotalPrice())
                .build();
        cartEventPublisher.publishCartCreated(event);

        return saved.getId();
    }

    @Override
    public void removeItemFromCart(String itemId) {
        CartItem cartItem = cartItemService.findById(itemId);
        if (cartItem == null) {
            throw new EntityNotFoundException("Cart item not found with ID: " + itemId);
        }

        Cart cart = cartItem.getCart();
        if (cart != null) {
            cart.getItems().removeIf(item -> item.getId().equals(itemId));
            cart.recalculateTotalPrice();
            cartRepository.save(cart);
        }

        cartItemService.removeCartItem(itemId); // cleanup
    }


    @Override
    public void clearCart(String cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found with ID: " + cartId));

        cart.getItems().clear(); // orphanRemoval will delete items
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);

        // ✅ Publish event
        CartClearedEvent clearedEvent = CartClearedEvent.builder()
                .cartId(cart.getId())
                .userId(cart.getUserId())
                .build();
        cartEventPublisher.publishCartCleared(clearedEvent);
    }

    @Override
    public CartResponse getCartById(String cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found with ID: " + cartId));

        // Build response manually
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(item -> CartItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .build())
                .toList();

        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .totalPrice(cart.getTotalPrice())
                .items(itemResponses)
                .build();
    }

    @Override
    public CartResponse updateCartItemQuantity(String cartItemId, int newQuantity) {
        if (newQuantity <= 0) {
            throw new BadApiRequestException("Quantity must be greater than zero.");
        }

        CartItem cartItem = cartItemService.findById(cartItemId);
        cartItem.setQuantity(newQuantity);

        Cart cart = cartItem.getCart();
        cart.recalculateTotalPrice();
        cartRepository.save(cart);

        return getCartById(cart.getId());
    }

    @Override
    public CartResponse getCartByUserId(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found for user ID: " + userId));

        return getCartById(cart.getId()); // Reuse existing logic
    }

    @Override
    public boolean cartExistsByUserId(String userId) {
        return cartRepository.findByUserId(userId).isPresent();
    }

    @Override
    public List<CartResponse> getAllCarts() {
        return cartRepository.findAll().stream()
                .map(cart -> CartResponse.builder()
                        .id(cart.getId())
                        .userId(cart.getUserId())
                        .totalPrice(cart.getTotalPrice())
                        .items(cart.getItems().stream()
                                .map(item -> CartItemResponse.builder()
                                        .id(item.getId())
                                        .productId(item.getProductId())
                                        .productName(item.getProductName())
                                        .price(item.getPrice())
                                        .quantity(item.getQuantity())
                                        .build())
                                .toList())
                        .build())
                .toList();
    }



}
