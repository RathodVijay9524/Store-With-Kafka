package in.vijay.order_service.repository;

import in.vijay.dto.order.OrderStatus;
import in.vijay.order_service.beans.Order;
import in.vijay.repository.GenericRepository;
import jakarta.persistence.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.util.Optional;

public interface OrderRepository extends GenericRepository<Order,String> {
    boolean existsByUserIdAndStatus(String userId, OrderStatus status);

    /*@Cacheable("orders")
    Optional<Order> findById(String id);

    @CacheEvict(value = "orders", key = "#order.id")
    Order save(Order order);
*/

}
