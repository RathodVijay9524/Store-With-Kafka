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

    public InventoryService(InventoryHttpClient inventoryHttpClient) {
        this.inventoryHttpClient = inventoryHttpClient;

    }

    public void validateAndReserveItem(CartItemResponse item) {
        InventoryRequest request = new InventoryRequest(item.getProductId(), item.getQuantity());
        InventoryResponse response = inventoryHttpClient.checkInventory(request);

        if (!response.isAvailable()) {
            log.error("❌ Inventory unavailable for product: {}", item.getProductId());
            throw new BadApiRequestException("Inventory not available for product: " + item.getProductId());
        }

         log.debug("✅ Inventory available  for product: {}", item.getProductId());
    }

}

