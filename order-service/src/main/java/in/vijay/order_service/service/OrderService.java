package in.vijay.order_service.service;

import in.vijay.dto.order.OrderRequest;
import in.vijay.dto.order.OrderResponse;
import java.util.List;

public interface OrderService {

    OrderResponse placeOrderFromCart(OrderRequest request);

    OrderResponse getOrderById(String orderId);

    List<OrderResponse> getAllOrders();

    OrderResponse updateOrderStatus(String orderId, String status);

    void cancelOrder(String orderId, String reason);

    void deleteOrder(String orderId);

    List<OrderResponse> getOrdersByUserId(String userId);
}
