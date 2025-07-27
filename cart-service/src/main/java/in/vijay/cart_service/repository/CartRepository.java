package in.vijay.cart_service.repository;


import in.vijay.cart_service.beans.Cart;
import in.vijay.repository.GenericRepository;


import java.util.Optional;

public interface CartRepository extends GenericRepository<Cart,String> {

    Optional<Cart> findByUserId(String userId);
}

