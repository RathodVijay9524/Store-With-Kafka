package in.vijay.order_service.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Slf4j
public class OrderServiceImpl1 {

    /*private final UserHttpClient userHttpClient;
    private final CartHttpClient cartHttpClient;
    private final ProductHttpClient productHttpClient;
    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final OrderEventPublisher orderEventPublisher;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;
    private final InventoryHttpClient inventoryHttpClient;

    @Transactional(rollbackFor = Exception.class)
    public OrderResponse placeOrderFromCart(OrderRequest request) {
        validateOrderRequest(request);
        log.info("🛒 Starting order placement for user: {}", request.getUserId());

        List<ReservationInfo> reservedInventoryItems = new ArrayList<>();

        try {
            // 1. Validate user and retrieve cart concurrently
            CompletableFuture<UserResponse> userFuture = validateUserAsync(request.getUserId());
            CompletableFuture<CartResponse> cartFuture = retrieveCartAsync(request.getUserId());

            UserResponse user = userFuture.join();
            CartResponse cart = cartFuture.join();

            // 2. Validate products and reserve inventory
            reservedInventoryItems = validateProductsAndReserveInventory(cart.getItems());

            // 3. Create and save order
            Order order = createOrderEntity(request.getUserId());
            Order savedOrder = orderRepository.save(order);

            // 4. Create order items
            List<OrderItem> orderItems = createOrderItems(savedOrder, cart.getItems());
            savedOrder.setItems(orderItems);
            savedOrder.calculateTotalAmount();

            Order finalOrder = orderRepository.save(savedOrder);
            log.info("✅ Order created with ID: {} and Total: {}", finalOrder.getId(), finalOrder.getTotalAmount());

            // 5. Post-order processing (async where possible)
            performPostOrderProcessing(cart, finalOrder);

            return modelMapper.map(finalOrder, OrderResponse.class);

        } catch (Exception e) {
            log.error("❌ Order placement failed for user: {}", request.getUserId(), e);
            rollbackInventoryReservations(reservedInventoryItems);
            throw handleOrderPlacementException(e, request.getUserId());
        }
    }

    private void validateOrderRequest(OrderRequest request) {
        if (request == null || StringUtils.isBlank(request.getUserId())) {
            throw new BadApiRequestException("Invalid order request: User ID is required");
        }
    }

    private CompletableFuture<UserResponse> validateUserAsync(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            log.debug("🔍 Validating user existence...");
            try {
                UserResponse user = userHttpClient.getUserById(userId);
                if (user == null) {
                    log.error("❌ User not found: {}", userId);
                    throw new EntityNotFoundException("User not found: " + userId);
                }
                return user;
            } catch (Exception e) {
                log.error("❌ Error validating user: {}", userId, e);
                throw new ServiceException("Failed to validate user", e);
            }
        });
    }

    private CompletableFuture<CartResponse> retrieveCartAsync(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            log.debug("📦 Fetching cart for user: {}", userId);
            try {
                ApiResponse<CartResponse> cartData = cartHttpClient.getCartByUserId(userId);
                CartResponse cart = cartData.getData();

                if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
                    log.warn("⚠️ Empty cart for user: {}", userId);
                    throw new BadApiRequestException("Cart is empty. Cannot place order.");
                }

                return cart;
            } catch (Exception e) {
                log.error("❌ Error retrieving cart for user: {}", userId, e);
                throw new ServiceException("Failed to retrieve cart", e);
            }
        });
    }

    private List<ReservationInfo> validateProductsAndReserveInventory(List<CartItemResponse> cartItems) {
        log.info("🔍 Validating products and reserving inventory...");
        List<ReservationInfo> reservedItems = new ArrayList<>();
        List<CompletableFuture<ReservationInfo>> reservationFutures = new ArrayList<>();

        for (CartItemResponse item : cartItems) {
            CompletableFuture<ReservationInfo> future = CompletableFuture.supplyAsync(() ->
                    processCartItem(item, reservedItems)
            );
            reservationFutures.add(future);
        }

        try {
            // Wait for all reservations to complete
            List<ReservationInfo> allReservedItems = reservationFutures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());

            return allReservedItems;
        } catch (Exception e) {
            // If any reservation fails, rollback already reserved items
            rollbackInventoryReservations(reservedItems);
            throw new BadApiRequestException("Failed to reserve inventory for cart items");
        }
    }

    private ReservationInfo processCartItem(CartItemResponse item, List<ReservationInfo> reservedItems) {
        try {
            // Validate product
            ApiResponse<ProductResponse> productResponse = productHttpClient.getProductById(item.getProductId());
            ProductResponse product = productResponse.getData();

            if (product == null) {
                throw new EntityNotFoundException("Product not found: " + item.getProductId());
            }

            OrderValidator.validateCartItem(item, product);

            // Reserve inventory
            inventoryService.validateAndReserveItem(item);

            ReservationInfo reservationInfo = new ReservationInfo(item.getProductId(), item.getQuantity());

            synchronized (reservedItems) {
                reservedItems.add(reservationInfo);
            }

            log.debug("✅ Inventory reserved for product: {}", item.getProductId());
            return reservationInfo;

        } catch (Exception e) {
            log.error("❌ Failed to process cart item: {}", item.getProductId(), e);
            throw new RuntimeException("Failed to process item: " + item.getProductId(), e);
        }
    }

    private Order createOrderEntity(String userId) {
        log.info("🧾 Creating order entity...");
        Order order = Order.builder()
                .userId(userId)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.CREATED)
                .items(new ArrayList<>())
                .build();

        order.setId(generateDateBased("ORD"));
        return order;
    }

    private List<OrderItem> createOrderItems(Order savedOrder, List<CartItemResponse> cartItems) {
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItemResponse cartItem : cartItems) {
            try {
                OrderItemRequest itemRequest = OrderItemRequest.builder()
                        .productId(cartItem.getProductId())
                        .productName(cartItem.getProductName())
                        .price(cartItem.getPrice())
                        .quantity(cartItem.getQuantity())
                        .build();

                OrderItemResponse createdItem = orderItemService.addOrderItem(savedOrder.getId(), itemRequest);
                OrderItem entity = modelMapper.map(createdItem, OrderItem.class);
                entity.setOrder(savedOrder);
                orderItems.add(entity);

            } catch (Exception e) {
                log.error("❌ Failed to create order item for product: {}", cartItem.getProductId(), e);
                throw new ServiceException("Failed to create order item", e);
            }
        }

        return orderItems;
    }

    private void performPostOrderProcessing(CartResponse cart, Order finalOrder) {
        // Clear cart asynchronously
        CompletableFuture.runAsync(() -> clearCart(cart))
                .exceptionally(throwable -> {
                    log.error("⚠️ Failed to clear cart: {}", cart.getId(), throwable);
                    return null;
                });

        // Publish order event asynchronously
        CompletableFuture.runAsync(() -> publishOrderCreatedEvent(finalOrder))
                .exceptionally(throwable -> {
                    log.error("⚠️ Failed to publish order event: {}", finalOrder.getId(), throwable);
                    return null;
                });
    }

    private void clearCart(CartResponse cart) {
        try {
            cartHttpClient.clearCart(cart.getId());
            log.info("🧹 Cart cleared for cart ID: {}", cart.getId());
        } catch (Exception e) {
            log.error("❌ Error clearing cart: {}", cart.getId(), e);
            // Consider implementing retry mechanism or compensating transaction
            throw new ServiceException("Failed to clear cart", e);
        }
    }

    private void publishOrderCreatedEvent(Order order) {
        try {
            orderEventPublisher.publishOrderCreatedEvent(order);
            log.info("📣 OrderCreatedEvent published for order ID: {}", order.getId());
        } catch (Exception e) {
            log.error("❌ Error publishing order event: {}", order.getId(), e);
            // Consider implementing retry mechanism
            throw new ServiceException("Failed to publish order event", e);
        }
    }

    private void rollbackInventoryReservations(List<ReservationInfo> reservedItems) {
        if (reservedItems == null || reservedItems.isEmpty()) {
            return;
        }

        log.warn("🔄 Rolling back inventory reservations for {} items", reservedItems.size());

        for (ReservationInfo reservation : reservedItems) {
            try {
               *//* InventoryRequest releaseRequest = new InventoryRequest(reservation.getProductId(), reservation.getQuantity());
                inventoryHttpClient.releaseReservedInventory(releaseRequest);
                log.debug("✅ Released inventory reservation for product: {} (quantity: {})",
                        reservation.getProductId(), reservation.getQuantity());*//*
            } catch (Exception e) {
               *//* log.error("⚠️ Failed to release inventory reservation for product: {} (quantity: {})",
                        reservation.getProductId(), reservation.getQuantity(), e);*//*
                // Log for manual intervention but don't fail the rollback process
            }
        }
    }

    @Data
    @AllArgsConstructor
    private static class ReservationInfo {
        private String productId;
        private Integer quantity;
    }

    private RuntimeException handleOrderPlacementException(Exception e, String userId) {
        if (e instanceof BadApiRequestException || e instanceof EntityNotFoundException) {
            return (RuntimeException) e;
        }

        // Wrap unexpected exceptions
        return new ServiceException("Order placement failed for user: " + userId, e);
    }

    private String generateDateBased(String prefix) {
        // Consider using a more robust ID generation strategy for production
        LocalDateTime now = LocalDateTime.now();
        String dateTime = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomSuffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return String.format("%s-%s-%s", prefix, dateTime, randomSuffix);
    }*/
}

