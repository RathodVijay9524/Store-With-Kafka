package in.vijay.event.payments;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCompletedEvent {
    private String orderId;
    private String paymentId;
    private Double paidAmount;
    private String paymentDate;
    private String paymentMethod;
    private String productId;
    private Integer quantity;
}

