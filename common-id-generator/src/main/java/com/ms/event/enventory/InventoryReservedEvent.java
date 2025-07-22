package com.ms.event.enventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryReservedEvent {
    private String orderId;
    private String productId;
    private Integer reservedQuantity;
    private String reservedDate;
}

