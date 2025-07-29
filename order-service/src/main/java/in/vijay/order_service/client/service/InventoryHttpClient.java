package in.vijay.order_service.client.service;

import in.vijay.dto.inventory.InventoryRequest;
import in.vijay.dto.inventory.InventoryResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange
public interface InventoryHttpClient {

    @PostExchange("/check")
    InventoryResponse checkInventory(@RequestBody InventoryRequest request);
}

