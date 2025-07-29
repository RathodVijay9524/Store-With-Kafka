package in.vijay.order_service.service;

import in.vijay.dto.order.OrderItemRequest;
import in.vijay.dto.order.OrderItemResponse;

import java.util.List;

public interface OrderItemService {

    OrderItemResponse addOrderItem(String orderId, OrderItemRequest request);

    OrderItemResponse getOrderItemById(String itemId);

    List<OrderItemResponse> getAllItemsForOrder(String orderId);

    OrderItemResponse updateOrderItem(String itemId, OrderItemRequest request);

    void deleteOrderItem(String itemId);
}
