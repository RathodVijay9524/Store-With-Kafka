package in.vijay.order_service.util;

import in.vijay.dto.order.*;
import in.vijay.dto.product.ProductResponse;

import in.vijay.exceptions.BadApiRequestException;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

@Service
public class OrderValidator {

    public void validateCartItem(CartItemResponse item, ProductResponse product) {
        if (product == null) {
            throw new EntityNotFoundException("❌ Product not found: " + item.getProductId());
        }

        if (product.getPrice() == null || product.getPrice().compareTo(item.getPrice()) != 0) {
            throw new BadApiRequestException("❗ Price mismatch for product [" + item.getProductId() + "]");
        }

        if (item.getQuantity() <= 0 || item.getQuantity() > 10) {
            throw new BadApiRequestException("❗ Invalid quantity [" + item.getQuantity() + "] for product: " + item.getProductId());
        }
    }

}

/*
@Override
    public OrderResponse placeOrderFromCart(OrderRequest request) {

        log.info("🛒 Starting order placement for user: {}", request.getUserId());

        // 1. Validate User
        log.debug("🔍 Validating user existence...");
        UserResponse user = userHttpClient.getUserById(request.getUserId());
        if (user == null) {
            log.error("❌ User not found: {}", request.getUserId());
            throw new EntityNotFoundException("User not found");
        }

        // 2. Retrieve User's Cart
        log.debug("📦 Fetching cart for user: {}", request.getUserId());
        ApiResponse<CartResponse> cartData = cartHttpClient.getCartByUserId(request.getUserId());
        CartResponse cart = cartData.getData();
        if (cart == null || cart.getItems().isEmpty()) {
            log.warn("⚠️ Empty cart for user: {}", request.getUserId());
            throw new BadApiRequestException("Cart is empty. Cannot place order.");
        }

        // 3. Validate Inventory and Product
        log.info("🔍 Validating products and reserving inventory...");
        for (CartItemResponse item : cart.getItems()) {
            ApiResponse<ProductResponse> productResponse = productHttpClient.getProductById(item.getProductId());
            ProductResponse product = productResponse.getData();
            OrderValidator.validateCartItem(item, product); // price, null check, quantity check
            inventoryService.validateAndReserveItem(item);
        }

        // 4. Create Order
        log.info("🧾 Creating order entity...");
        Order order = Order.builder()
                .userId(request.getUserId())
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.CREATED)
                .items(new ArrayList<>()) // will be populated after item creation
                .build();
        order.setId(generateDateBased("ORD"));

        // Save the order skeleton to attach items
        Order savedOrder = orderRepository.save(order);

        // 5. Create and attach OrderItems using service
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItemResponse cartItem : cart.getItems()) {
            OrderItemRequest itemRequest = OrderItemRequest.builder()
                    .productId(cartItem.getProductId())
                    .productName(cartItem.getProductName())
                    .price(cartItem.getPrice())
                    .quantity(cartItem.getQuantity())
                    .build();

            OrderItemResponse createdItem = orderItemService.addOrderItem(savedOrder.getId(), itemRequest);
            OrderItem entity = modelMapper.map(createdItem, OrderItem.class);
            entity.setOrder(savedOrder); // maintain bidirectional link
            orderItems.add(entity);
        }

        savedOrder.setItems(orderItems);
        savedOrder.calculateTotalAmount();
        Order finalSavedOrder = orderRepository.save(savedOrder); // save updated amount + items

        log.info("✅ Order created with ID: {} and Total: {}", finalSavedOrder.getId(), finalSavedOrder.getTotalAmount());

        // 6. Clear cart
        cartHttpClient.clearCart(cart.getId());
        log.info("🧹 Cart cleared for user: {}", request.getUserId());

        // 7. Publish Order Created Event
        orderEventPublisher.publishOrderCreatedEvent(finalSavedOrder);
        log.info("📣 OrderCreatedEvent published for order ID: {}", finalSavedOrder.getId());

        // 8. Return Response
        return modelMapper.map(finalSavedOrder, OrderResponse.class);
    }


*/




  /*  public static void validateCartItem(CartItemResponse item, ProductResponse product) {
        if (product == null) throw new EntityNotFoundException("Product not found");
        if (!product.getPrice().equals(item.getPrice())) throw new BadApiRequestException("Price mismatch");
        if (item.getQuantity() <= 0) throw new BadApiRequestException("Invalid quantity");
    }*/

