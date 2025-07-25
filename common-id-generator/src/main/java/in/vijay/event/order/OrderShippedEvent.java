package in.vijay.event.order;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderShippedEvent {
    private String orderId;
    private String shippingId;
    private String shippedDate;
    private String deliveryEstimate;
    private String address;
}

