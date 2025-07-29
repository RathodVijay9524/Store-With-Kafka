package in.vijay.order_service.service;

import in.vijay.dto.inventory.InventoryRequest;
import in.vijay.dto.inventory.InventoryResponse;
import in.vijay.dto.order.CartItemResponse;
import in.vijay.event.enventory.InventoryReservedEvent;
import in.vijay.exceptions.BadApiRequestException;
import in.vijay.order_service.client.service.InventoryHttpClient;
import in.vijay.order_service.event.OrderEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InventoryService {

    private final InventoryHttpClient inventoryHttpClient;
    private final OrderEventPublisher orderEventPublisher;

    public InventoryService(InventoryHttpClient inventoryHttpClient, OrderEventPublisher orderEventPublisher) {
        this.inventoryHttpClient = inventoryHttpClient;
        this.orderEventPublisher = orderEventPublisher;
    }

    public void validateAndReserveItem(CartItemResponse item) {
        InventoryRequest request = new InventoryRequest(item.getProductId(), item.getQuantity());
        InventoryResponse response = inventoryHttpClient.checkInventory(request);

        if (!response.isAvailable()) {
            log.error("❌ Inventory unavailable for product: {}", item.getProductId());
            throw new BadApiRequestException("Inventory not available for product: " + item.getProductId());
        }

        inventoryHttpClient.reserveInventory(request);

        // ✅ Publish reserved event (orderId can be updated from context)
        orderEventPublisher.publishInventoryReservedEvent(
                InventoryReservedEvent.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .orderId(null) // pass actual orderId if already available
                        .build()
        );

        log.debug("✅ Inventory reserved for product: {}", item.getProductId());
    }

}

