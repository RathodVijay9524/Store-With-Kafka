package in.vijay.event.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCompletedEvent {

    private String orderId;
    private String shipmentId;
    private String completedDate;
    private String productId;


}
