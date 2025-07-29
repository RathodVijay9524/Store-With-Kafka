package in.vijay.order_service.service;
import in.vijay.dto.ApiResponse;
import in.vijay.dto.inventory.InventoryRequest;
import in.vijay.dto.order.*;
import in.vijay.dto.product.ProductResponse;
import in.vijay.dto.user.UserResponse;
import in.vijay.event.enventory.InventoryReservedEvent;
import in.vijay.exceptions.BadApiRequestException;
import in.vijay.order_service.beans.Order;
import in.vijay.order_service.beans.OrderItem;
import in.vijay.order_service.client.service.CartHttpClient;
import in.vijay.order_service.client.service.InventoryHttpClient;
import in.vijay.order_service.client.service.ProductHttpClient;
import in.vijay.order_service.client.service.UserHttpClient;
import in.vijay.order_service.event.OrderEventPublisher;
import in.vijay.order_service.repository.OrderRepository;
import in.vijay.order_service.util.OrderValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.CacheManager;
import org.springframework.core.task.TaskExecutor;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


import static in.vijay.order_service.util.PrefixGenerator.generateDateBased;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl2 {

/*    private final UserHttpClient userHttpClient;
    private final CartHttpClient cartHttpClient;
    private final ProductHttpClient productHttpClient;
    private final InventoryHttpClient inventoryHttpClient;
    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final OrderEventPublisher orderEventPublisher;
    private final ModelMapper modelMapper;
    private final RetryTemplate retryTemplate;
    private final TaskExecutor taskExecutor;
    private final CacheManager cacheManager;

    @Transactional(isolation = Isolation.SERIALIZABLE, timeout = 30)
    public OrderResponse placeOrderFromCart(String userId) {
      *//*  try {
            // 1. Validate User with caching
            UserResponse user = getUserWithCache(userId);
            log.info("🛒 Starting order placement for user: {}", userId);

            // 2. Retrieve and validate User's Cart
            CartResponse cart = getValidCart(userId);

            // 3. Parallel inventory validation and reservation
            validateAndReserveInventoryInParallel(cart);

            // 4. Create Order
            Order order = createOrder(userId);

            // 5. Create and attach OrderItems in batch
            addOrderItemsInBatch(order, cart);

            // 6. Clear cart
            clearCart(cart.getId());

            // 7. Publish Order Created Event
            publishOrderEvents(order);

            log.info("✅ Order successfully created with ID: {}", order.getId());
            return mapToResponse(order);

        } catch (Exception e) {
            log.error("❌ Order placement failed for user: {}", userId, e);
            throw e;
        }
    }

    private UserResponse getUserWithCache(String userId) {
        return cacheManager.getCache("users").get(userId, () -> {
            log.debug("🔍 Fetching user from service: {}", userId);
            UserResponse user = userHttpClient.getUserById(userId);
            if (user == null) {
                throw new EntityNotFoundException("User not found");
            }
            return user;
        });
    }

    private CartResponse getValidCart(String userId) {
        CartResponse cart = retryTemplate.execute(context ->
                cartHttpClient.getCartByUserId(userId));

        if (cart == null || cart.getItems().isEmpty()) {
            log.warn("⚠️ Empty cart for user: {}", userId);
            throw new BadApiRequestException("Cart is empty. Cannot place order.");
        }
        return cart;
    }

    private void validateAndReserveInventoryInParallel(CartResponse cart) {
        List<CompletableFuture<Void>> inventoryFutures = cart.getItems().parallelStream()
                .map(item -> CompletableFuture.runAsync(() ->
                        processInventoryItem(item), taskExecutor))
                .toList();

        try {
            CompletableFuture.allOf(inventoryFutures.toArray(new CompletableFuture[0])).get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Inventory processing failed", e);
            throw new OrderProcessingException("Inventory processing failed", e);
        }
    }

    private void processInventoryItem(CartItemResponse item) {
        try {
            // Get product with cache
            ProductResponse product = cacheManager.getCache("products").get(item.getProductId(), () ->
                    productHttpClient.getProductById(item.getProductId()).getData());

            OrderValidator.validateCartItem(item, product);

            // Inventory check with retry
            InventoryRequest inventoryRequest = new InventoryRequest(item.getProductId(), item.getQuantity());
            InventoryResponse inventoryResponse = retryTemplate.execute(context ->
                    inventoryHttpClient.checkInventory(inventoryRequest));

            if (!inventoryResponse.isAvailable()) {
                log.error("❌ Inventory unavailable for product: {}", item.getProductId());
                throw new InventoryUnavailableException("Inventory not available for product: " + item.getProductId());
            }

            // Reserve inventory with retry
            retryTemplate.execute(context -> {
                inventoryHttpClient.reserveInventory(inventoryRequest);
                orderEventPublisher.publishInventoryReservedEvent(
                        new InventoryReservedEvent(null, item.getProductId(), item.getQuantity()));
                return null;
            });

        } catch (Exception e) {
            log.error("Failed to process inventory for product: {}", item.getProductId(), e);
            throw new OrderProcessingException("Inventory processing failed for product: " + item.getProductId(), e);
        }
    }

    private Order createOrder(String userId) {
        Order order = Order.builder()

                .userId(userId)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.CREATED)
                .items(new ArrayList<>())
                .build();
        order.setId(generateDateBased("ORD"));

        return orderRepository.save(order);
    }

    private void addOrderItemsInBatch(Order order, CartResponse cart) {
        List<OrderItem> orderItems = new ArrayList<>();

        // Process items in batches of 10
        Lists.partition(cart.getItems(), 10).forEach(batch -> {
            batch.forEach(cartItem -> {
                OrderItemRequest itemRequest = OrderItemRequest.builder()
                        .productId(cartItem.getProductId())
                        .productName(cartItem.getProductName())
                        .price(cartItem.getPrice())
                        .quantity(cartItem.getQuantity())
                        .build();

                OrderItemResponse createdItem = orderItemService.addOrderItem(order.getId(), itemRequest);
                OrderItem entity = modelMapper.map(createdItem, OrderItem.class);
                entity.setOrder(order);
                orderItems.add(entity);
            });
        });

        order.setItems(orderItems);
        order.calculateTotalAmount();
        orderRepository.save(order);
    }

    private void clearCart(String cartId) {
        try {
            retryTemplate.execute(context -> {
                cartHttpClient.clearCart(cartId);
                return null;
            });
            log.info("🧹 Cart cleared successfully");
        } catch (Exception e) {
            log.warn("⚠️ Failed to clear cart, but order was placed. Cart ID: {}", cartId, e);
            // Continue despite cart clearing failure
        }
    }

    private void publishOrderEvents(Order order) {
        try {
            orderEventPublisher.publishOrderCreatedEvent(order);
            log.info("📣 OrderCreatedEvent published for order ID: {}", order.getId());
        } catch (Exception e) {
            log.error("Failed to publish order event", e);
            // Consider adding to dead letter queue or retry mechanism
        }
    }

    private OrderResponse mapToResponse(Order order) {
        OrderResponse response = modelMapper.map(order, OrderResponse.class);
       // response.setOrderStatus(order.getStatus().name());
        return response;
    }

    private String generateSequential(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Exception handler for compensation transactions
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleFailure(String userId, CartResponse cart, Exception e) {
        log.error("Order placement failed, executing compensation transactions", e);

        if (cart != null) {
            cart.getItems().forEach(item -> {
                try {
                    inventoryHttpClient.releaseInventory(new InventoryRequest(
                            item.getProductId(),
                            item.getQuantity()
                    ));
                    log.info("Released inventory for product: {}", item.getProductId());
                } catch (Exception ex) {
                    log.error("Failed to release inventory for product: {}", item.getProductId(), ex);
                }
            });
        }

        // orderEventPublisher.publishOrderFailedEvent(new OrderFailedEvent(userId, e.getMessage()));
    *//*

    }*/

}

