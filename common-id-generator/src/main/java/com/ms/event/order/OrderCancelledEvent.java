package com.ms.event.order;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCancelledEvent {
    private String orderId;
    private String reason;
    private String cancelledDate;
}

