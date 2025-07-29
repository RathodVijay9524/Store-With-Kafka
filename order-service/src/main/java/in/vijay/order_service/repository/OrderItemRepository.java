package in.vijay.order_service.repository;

import in.vijay.order_service.beans.OrderItem;
import in.vijay.repository.GenericRepository;

import java.util.List;

public interface OrderItemRepository extends GenericRepository<OrderItem,String> {

    List<OrderItem> findByOrderId(String orderId);
}
