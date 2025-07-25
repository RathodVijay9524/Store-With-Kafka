package in.vijay.event.enventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryRollbackEvent {
    private String orderId;
    private String productId;
    private Integer quantity;
    private String reason;
    private String rollbackDate;
}
