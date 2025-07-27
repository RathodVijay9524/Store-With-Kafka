package in.vijay.cart_service.service;


import in.vijay.dto.order.AddItemToCartRequest;
import in.vijay.dto.order.CartResponse;

import java.util.List;

public interface CartService {

    String createCart(String userId, AddItemToCartRequest request);
    void removeItemFromCart(String itemId);
    void clearCart(String cartId);
    CartResponse getCartById(String cartId);

    CartResponse updateCartItemQuantity(String cartItemId, int newQuantity);

    CartResponse getCartByUserId(String userId);

    boolean cartExistsByUserId(String userId);

    List<CartResponse> getAllCarts(); // Optional - for admin use

}

