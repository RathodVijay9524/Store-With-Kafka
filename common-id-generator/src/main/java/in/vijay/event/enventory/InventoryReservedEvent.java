package in.vijay.event.enventory;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryReservedEvent {
    private String orderId;
    private String productId;
    private Integer quantity;
    private Integer reservedQuantity;
    private String reservedDate;
}

