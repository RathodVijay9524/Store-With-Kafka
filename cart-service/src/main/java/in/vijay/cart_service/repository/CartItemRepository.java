package in.vijay.cart_service.repository;


import in.vijay.cart_service.beans.CartItem;
import in.vijay.repository.GenericRepository;

import java.util.List;

public interface CartItemRepository extends GenericRepository<CartItem,String> {
    List<CartItem> findByCartId(String cartId);
}

