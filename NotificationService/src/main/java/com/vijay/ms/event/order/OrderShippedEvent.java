package com.vijay.ms.event.order;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OrderShippedEvent extends OrderEvent {
    private String orderId;
    private String shippingId;
    private String shippedDate;
    private String deliveryEstimate;
    private String address;
}

