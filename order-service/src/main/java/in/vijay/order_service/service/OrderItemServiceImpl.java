package in.vijay.order_service.service;

import in.vijay.dto.ApiResponse;
import in.vijay.dto.order.OrderItemRequest;
import in.vijay.dto.order.OrderItemResponse;
import in.vijay.dto.product.ProductResponse;
import in.vijay.exceptions.BadApiRequestException;
import in.vijay.order_service.beans.Order;
import in.vijay.order_service.beans.OrderItem;
import in.vijay.order_service.client.service.IdGeneratorClient;
import in.vijay.order_service.client.service.ProductHttpClient;
import in.vijay.order_service.event.OrderEventPublisher;
import in.vijay.order_service.repository.OrderItemRepository;
import in.vijay.order_service.repository.OrderRepository;
import in.vijay.order_service.util.PrefixGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductHttpClient productHttpClient;
    private final OrderEventPublisher orderEventPublisher;
    private final ModelMapper modelMapper;
    private final IdGeneratorClient idGeneratorClient;

    @Override
    public OrderItemResponse addOrderItem(String orderId, OrderItemRequest request) {
        log.info("🧩 [AddItem] Initiating addition of item to orderId: {}", orderId);

        // 1️⃣ Validate Order Existence
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found for ID: " + orderId));
        log.info("✅ Order found: {}", orderId);

        // 2️⃣ Validate Product
        ApiResponse<ProductResponse> response = productHttpClient.getProductById(request.getProductId());
        ProductResponse product = (response != null) ? response.getData() : null;

        if (product == null) {
            throw new EntityNotFoundException("Product not found: " + request.getProductId());
        }
        log.info("📦 Product retrieved: {} - {}", product.getId(), product.getName());

        // 3️⃣ Validate Quantity and Price
        if (request.getQuantity() <= 0 || request.getQuantity() > 10) {
            throw new BadApiRequestException("Invalid quantity: " + request.getQuantity());
        }
        if (request.getPrice() == null || product.getPrice().compareTo(request.getPrice()) != 0) {
            throw new BadApiRequestException("Price mismatch for product: " + product.getId());
        }

        // 4️⃣ Create OrderItem Entity
        OrderItem item = OrderItem.builder()
                .productId(product.getId())
                .productName(product.getName())
                .price(product.getPrice())
                .quantity(request.getQuantity())
                .order(order)
                .build();
        item.setId(idGeneratorClient.generateDateBasedId("ITEM","ITEM"));
        OrderItem savedItem = orderItemRepository.save(item);
        log.info("📝 Order item saved: {} (productId: {})", savedItem.getId(), savedItem.getProductId());

        // 5️⃣ Publish Event (optional but helpful)
        try {
            orderEventPublisher.publishOrderItemShippedEvent(savedItem);
            log.info("📡 OrderItemShippedEvent published for itemId: {}", savedItem.getId());
        } catch (Exception ex) {
            log.warn("⚠️ Failed to publish OrderItemShippedEvent for itemId: {}", savedItem.getId(), ex);
        }
        // 6️⃣ Return Response
        return modelMapper.map(savedItem, OrderItemResponse.class);
    }

    @Override
    public OrderItemResponse getOrderItemById(String itemId) {
        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Order item not found with ID: " + itemId));

        return modelMapper.map(item, OrderItemResponse.class);
    }

    @Override
    public List<OrderItemResponse> getAllItemsForOrder(String orderId) {
        return orderItemRepository.findByOrderId(orderId).stream()
                .map(item -> modelMapper.map(item, OrderItemResponse.class))
                .toList();
    }

    @Override
    public OrderItemResponse updateOrderItem(String itemId, OrderItemRequest request) {
        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Order item not found with ID: " + itemId));

        item.setQuantity(request.getQuantity());
        item.setPrice(request.getPrice());
        item.setProductName(request.getProductName());

        OrderItem updated = orderItemRepository.save(item);

        return modelMapper.map(updated, OrderItemResponse.class);
    }

    @Override
    public void deleteOrderItem(String itemId) {
        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Order item not found with ID: " + itemId));

        orderItemRepository.delete(item);

        // Optional: publish cancellation event
        // orderEventPublisher.publishOrderItemCancelledEvent(item);
    }

}

