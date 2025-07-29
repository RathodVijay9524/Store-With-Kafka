package in.vijay.order_service.service;


import in.vijay.dto.order.*;
import in.vijay.dto.product.ProductResponse;
import in.vijay.dto.user.UserResponse;
import in.vijay.event.enventory.InventoryRollbackEvent;
import in.vijay.exceptions.BadApiRequestException;
import in.vijay.order_service.beans.Order;
import in.vijay.order_service.beans.OrderItem;
import in.vijay.order_service.client.service.CartHttpClient;
import in.vijay.order_service.client.service.InventoryHttpClient;
import in.vijay.order_service.client.service.ProductHttpClient;
import in.vijay.order_service.client.service.UserHttpClient;
import in.vijay.order_service.event.OrderEventPublisher;
import in.vijay.order_service.repository.OrderRepository;
import in.vijay.order_service.exceptions.OrderProcessingException;
import in.vijay.order_service.util.OrderValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;


import static in.vijay.order_service.util.PrefixGenerator.generateDateBased;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final UserHttpClient userHttpClient;
    private final CartHttpClient cartHttpClient;
    private final ProductHttpClient productHttpClient;
    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final OrderEventPublisher orderEventPublisher;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;
    private final OrderValidator orderValidator;

    @Override
    public OrderResponse placeOrderFromCart(OrderRequest request) {
        String userId = request.getUserId();
        log.info("🛒 [Step 0] Initiating order placement for user: {}", userId);

        List<CartItemResponse> reservedItems = new ArrayList<>();
        Order savedOrder = null;

        try {
            // 1️⃣ Validate Input & Idempotency
            log.info("🧾 [Step 1] Validating order request");
            // validateOrderRequest(request);

            // 2️⃣ Validate User Existence
            log.info("🔎 [Step 2] Validating user existence for userId: {}", userId);
            UserResponse user = validateUser(userId);
            log.info("✅ User validated: {}", userId);

            // 3️⃣ Retrieve and Validate Cart
            log.info("🛍️ [Step 3] Fetching cart for user: {}", userId);
            CartResponse cart = fetchValidCart(userId);
            log.info("📥 Cart contains {} items (cartId: {})", cart.getItems().size(), cart.getId());

            // 4️⃣ Validate Products & Reserve Inventory
            log.info("📦 [Step 4] Validating products and reserving inventory");
            reservedItems = validateAndReserveInventory(cart.getItems());
            log.info("✅ Reserved inventory for {} items", reservedItems.size());

            // 5️⃣ Create Order Skeleton
            log.info("🧱 [Step 5] Creating order skeleton for user: {}", userId);
            Order order = createOrderSkeleton(userId);
            savedOrder = orderRepository.save(order);
            log.info("🆔 Order skeleton saved with ID: {}", savedOrder.getId());

            // 6️⃣ Map Items & Finalize Order
            log.info("🧩 [Step 6] Mapping cart items to order and calculating total");
            List<OrderItem> orderItems = createOrderItems(savedOrder, cart.getItems());
            //savedOrder.setItems(orderItems);
            savedOrder.getItems().clear();               // ✅ Clear existing
            savedOrder.getItems().addAll(orderItems);    // ✅ Add new items in-place
            savedOrder.calculateTotalAmount();
            Order finalOrder = orderRepository.save(savedOrder);
            log.info("💰 Final order total: {} (orderId: {})", finalOrder.getTotalAmount(), finalOrder.getId());

            // 7️⃣ Clear Cart
            log.info("🧹 [Step 7] Clearing cart for cartId: {}", cart.getId());
            cartHttpClient.clearCart(cart.getId());
            log.info("🧺 Cart cleared successfully");

            // 8️⃣ Publish Event
            log.info("📡 [Step 8] Publishing OrderCreatedEvent for orderId: {}", finalOrder.getId());
            orderEventPublisher.publishOrderCreatedEvent(finalOrder);

            // 9️⃣ Return Response
            log.info("✅ [Step 9] Order placement successful for user: {}", userId);
            return modelMapper.map(finalOrder, OrderResponse.class);

        } catch (Exception ex) {
            log.error("🔥 [Error] Order placement failed for user: {} - Reason: {}", userId, ex.getMessage());

            // 🔁 Rollback Reserved Inventory
            log.info("🔄 Rolling back reserved inventory");
            rollbackReservedInventory(reservedItems);

            // 🧨 Cancel Incomplete Order
            log.info("🛑 Handling failure and canceling incomplete order if needed");
            handleFailure(reservedItems, savedOrder, ex, userId);

            throw new OrderProcessingException("Order placement failed: " + ex.getMessage(), ex);
        }
    }

    private void validateOrderRequest(OrderRequest request) {
        if (request == null) {
            log.error("❌ Order request is null");
            throw new IllegalArgumentException("Order request cannot be null");
        }

        String userId = request.getUserId();
        if (userId == null || userId.trim().isEmpty()) {
            log.error("🚫 User ID is missing or empty in the order request");
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }

        log.debug("🔎 Checking for idempotency for user: {}", userId);
        if (orderRepository.existsByUserIdAndStatus(userId, OrderStatus.CREATED)) {
            log.warn("⚠️ Duplicate order attempt detected for user: {}", userId);
            throw new OrderProcessingException("Order already in progress for user: " + userId);
        }

        log.info("✅ Order request validated successfully for user: {}", userId);
    }

    private void handleFailure(List<CartItemResponse> reservedItems, Order savedOrder, Exception ex, String userId) {
        rollbackReservedInventory(reservedItems);

        if (savedOrder != null) {
            savedOrder.setStatus(OrderStatus.FAILED);
            orderRepository.save(savedOrder);
            orderEventPublisher.publishOrderCancelledEvent(savedOrder, "Order placement failed due to exception");
        }

        log.error("🔥Order placement failed for user: {} - Reason: {}", userId, ex.getMessage());
    }

    private void rollbackReservedInventory(List<CartItemResponse> reservedItems) {
        for (CartItemResponse item : reservedItems) {
            try {
               // inventoryHttpClient.rollbackInventory(item); // assumes rollback logic in InventoryService
                orderEventPublisher.publishInventoryRollbackEvent(
                        InventoryRollbackEvent.builder()
                                .productId(item.getProductId())
                                .reason("Rolling back due to order failure")
                                .build()
                );
                log.info("↩️ Rolled back inventory for product: {}", item.getProductId());
            } catch (Exception rollbackEx) {
                log.error("❗Failed to rollback inventory for product: {}", item.getProductId(), rollbackEx);
            }
        }
    }

    public List<CartItemResponse> validateAndReserveInventory(List<CartItemResponse> items) {
        log.info("🔍 Validating products and reserving inventory....");

        // 🧠 Parallel fetch product details
        Map<String, ProductResponse> productMap = fetchAndValidateProducts(items);

        List<CartItemResponse> reservedItems = new ArrayList<>();

        // 🎯 Sequentially validate and reserve
        for (CartItemResponse item : items) {
            ProductResponse product = productMap.get(item.getProductId());

            // Validate product existence, price match, quantity
            orderValidator.validateCartItem(item, product);

            // Reserve inventory and publish event
           // inventoryService.validateAndReserveItem(item);

            reservedItems.add(item);
        }

        return reservedItems;
    }

    private Map<String, ProductResponse> fetchAndValidateProducts(List<CartItemResponse> items) {
        return items.stream()
                .map(item -> CompletableFuture.supplyAsync(() ->
                        productHttpClient.getProductById(item.getProductId()).getData()
                ))
                .map(CompletableFuture::join)
                .collect(Collectors.toMap(ProductResponse::getId, Function.identity()));
    }

    @Override
    public OrderResponse getOrderById(String orderId) {
        return null;
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return List.of();
    }

    @Override
    public OrderResponse updateOrderStatus(String orderId, String status) {
        return null;
    }

    @Override
    public void cancelOrder(String orderId, String reason) {

    }

    @Override
    public void deleteOrder(String orderId) {

    }

    @Override
    public List<OrderResponse> getOrdersByUserId(String userId) {
        return List.of();
    }

    private UserResponse validateUser(String userId) {
        UserResponse user = userHttpClient.getUserById(userId);
        if (user == null) {
            throw new EntityNotFoundException("User not found");
        }
        return user;
    }

    private CartResponse fetchValidCart(String userId) {
        CartResponse cart = cartHttpClient.getCartByUserId(userId).getData();
        if (cart == null || cart.getItems().isEmpty()) {
            throw new BadApiRequestException("Cart is empty. Cannot place order.");
        }
        return cart;
    }

    private Order createOrderSkeleton(String userId) {
        String orderId = generateDateBased("ORD");
        Order order = Order.builder()
                .userId(userId)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.CREATED)
                .items(new ArrayList<>())
                .build();
        order.setId(orderId);
        return order;

    }

    private List<OrderItem> createOrderItems(Order order, List<CartItemResponse> cartItems) {
        return cartItems.stream().map(cartItem -> {
            OrderItemRequest itemRequest = OrderItemRequest.builder()
                    .productId(cartItem.getProductId())
                    .productName(cartItem.getProductName())
                    .price(cartItem.getPrice())
                    .quantity(cartItem.getQuantity())
                    .build();

            OrderItemResponse createdItem = orderItemService.addOrderItem(order.getId(), itemRequest);
            OrderItem entity = modelMapper.map(createdItem, OrderItem.class);
            entity.setOrder(order);
            return entity;
        }).collect(Collectors.toList());
    }


}

/*

@@Override
public OrderResponse placeOrderFromCart(OrderRequest request) {
    String userId = request.getUserId();
    log.info("🛒 [Step 0] Initiating order placement for user: {}", userId);

    List<CartItemResponse> reservedItems = new ArrayList<>();
    Order savedOrder = null;

    try {
        // 1️⃣ Validate Order Request
        if (userId == null || userId.isEmpty()) {
            throw new BadApiRequestException("User ID cannot be null or empty");
        }

        // 2️⃣ Validate User
        log.info("🔎 [Step 2] Validating user existence for userId: {}", userId);
        UserResponse user = userHttpClient.getUserById(userId);
        if (user == null) {
            throw new EntityNotFoundException("User not found for ID: " + userId);
        }

        // 3️⃣ Fetch & Validate Cart
        log.info("🛍️ [Step 3] Fetching cart for user: {}", userId);
        ApiResponse<CartResponse> cartData = cartHttpClient.getCartByUserId(userId);
        CartResponse cart = cartData.getData();
        if (cart == null || cart.getItems().isEmpty()) {
            throw new BadApiRequestException("Cart is empty. Cannot place order.");
        }
        log.info("📥 Cart contains {} items (cartId: {})", cart.getItems().size(), cart.getId());

        // 4️⃣ Validate Products & Reserve Inventory
        for (CartItemResponse item : cart.getItems()) {
            ApiResponse<ProductResponse> productResponse = productHttpClient.getProductById(item.getProductId());
            ProductResponse product = productResponse.getData();

            if (product == null) throw new EntityNotFoundException("Product not found: " + item.getProductId());
            if (!product.getPrice().equals(item.getPrice())) {
                throw new BadApiRequestException("Price mismatch for product: " + item.getProductId());
            }
            if (item.getQuantity() <= 0) {
                throw new BadApiRequestException("Invalid quantity for product: " + item.getProductId());
            }

            // Reserve Inventory
            InventoryRequest inventoryRequest = new InventoryRequest(item.getProductId(), item.getQuantity());
            InventoryResponse inventoryResponse = inventoryHttpClient.checkInventory(inventoryRequest);
            if (!inventoryResponse.isAvailable()) {
                throw new BadApiRequestException("Inventory not available for product: " + item.getProductId());
            }

            inventoryHttpClient.reserveInventory(inventoryRequest);
            orderEventPublisher.publishInventoryReservedEvent(
                    new InventoryReservedEvent(null, item.getProductId(), item.getQuantity())
            );
            reservedItems.add(item);
        }
        log.info("✅ Reserved inventory for {} items", reservedItems.size());

        // 5️⃣ Create Order Skeleton
        Order order = Order.builder()
                .id(generateSequential("ORD"))
                .userId(userId)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.CREATED)
                .items(new ArrayList<>())
                .build();
        savedOrder = orderRepository.save(order);
        log.info("🆔 Order skeleton saved with ID: {}", savedOrder.getId());

        // 6️⃣ Create Order Items
        List<OrderItem> orderItems = cart.getItems().stream().map(item -> {
            OrderItem orderItem = OrderItem.builder()
                    .id(generateSequential("ITEM"))
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .price(item.getPrice())
                    .quantity(item.getQuantity())
                    .order(savedOrder)
                    .build();
            return orderItem;
        }).toList();

        savedOrder.setItems(orderItems);
        savedOrder.calculateTotalAmount();
        Order finalOrder = orderRepository.save(savedOrder);
        log.info("💰 Final order total: {} (orderId: {})", finalOrder.getTotalAmount(), finalOrder.getId());

        // 7️⃣ Clear Cart
        cartHttpClient.clearCart(cart.getId());
        log.info("🧹 Cart cleared successfully for cartId: {}", cart.getId());

        // 8️⃣ Publish Order Created Event
        orderEventPublisher.publishOrderCreatedEvent(finalOrder);
        log.info("📣 OrderCreatedEvent published for orderId: {}", finalOrder.getId());

        // 9️⃣ Return Response
        return modelMapper.map(finalOrder, OrderResponse.class);

    } catch (Exception ex) {
        log.error("🔥 [Error] Order placement failed for user: {} - Reason: {}", userId, ex.getMessage());

        // 🔁 Rollback Inventory
        for (CartItemResponse item : reservedItems) {
            orderEventPublisher.publishInventoryRollbackEvent(
                    InventoryRollbackEvent.builder()
                            .productId(item.getProductId())
                            .reason("Order failed, rolling back reserved inventory")
                            .build()
            );
        }

        // ❌ Cancel Incomplete Order
        if (savedOrder != null) {
            savedOrder.setStatus(OrderStatus.FAILED);
            orderRepository.save(savedOrder);
            orderEventPublisher.publishOrderCancelledEvent(savedOrder, "Order placement failed");
        }

        throw new OrderProcessingException("Order placement failed: " + ex.getMessage(), ex);
    }
}

private String generateSequential(String prefix) {
    return prefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
}

*/


/*@Override
public OrderResponse placeOrderFromCart(OrderRequest request) {
    orderValidator.validateOrderRequest(request);
    log.info("🛒 Starting order placement for user: {}", request.getUserId());

    List<OrderValidator.ReservationInfo> reservedInventoryItems = new ArrayList<>();

    try {
        // 1. Validate user and retrieve cart concurrently
        CompletableFuture<UserResponse> userFuture = orderValidator.validateUserAsync(request.getUserId());
        CompletableFuture<CartResponse> cartFuture = orderValidator.retrieveCartAsync(request.getUserId());
        UserResponse user = userFuture.join();
        CartResponse cart = cartFuture.join();

        // 2. Validate products and reserve inventory
        reservedInventoryItems = orderValidator.validateProductsAndReserveInventory(cart.getItems());

        // 3. Create and save order
        Order order = orderValidator.createOrderEntity(request.getUserId());
        Order savedOrder = orderRepository.save(order);

        // 4. Create order items
        List<OrderItem> orderItems = orderValidator.createOrderItems(savedOrder, cart.getItems());
        savedOrder.setItems(orderItems);
        savedOrder.calculateTotalAmount();

        Order finalOrder = orderRepository.save(savedOrder);
        log.info("✅ Order created with ID: {} and Total: {}", finalOrder.getId(), finalOrder.getTotalAmount());

        // 5. Post-order processing (async where possible)
        orderValidator.performPostOrderProcessing(cart, finalOrder);

        return modelMapper.map(finalOrder, OrderResponse.class);

    } catch (Exception e) {
        log.error("❌ Order placement failed for user: {}", request.getUserId(), e);
        OrderValidator.rollbackInventoryReservations(reservedInventoryItems);
        throw orderValidator.handleOrderPlacementException(e, request.getUserId());
    }
}*/

