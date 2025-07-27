package in.vijay.cart_service.service;

import in.vijay.cart_service.beans.CartItem;

public interface CartItemService {
    CartItem createCartItem(String productId, int quantity);
    void removeCartItem(String cartItemId);
    CartItem findById(String id);
}
