package com.ms.event.payments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundRequestedEvent {
    private String orderId;
    private String paymentId;
    private String reason;
    private String requestedDate;
}

