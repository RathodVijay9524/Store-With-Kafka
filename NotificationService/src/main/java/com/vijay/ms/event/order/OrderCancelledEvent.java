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
public class OrderCancelledEvent extends OrderEvent {
    private String orderId;
    private String reason;
    private String cancelledDate;
}

