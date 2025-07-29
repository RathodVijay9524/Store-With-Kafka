package com.vijay.ms.service;



import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

    public void notifyShipped(String orderId, String shippingId, String address) {
        log.info("📦 Notification: Order [{}] has been shipped via [{}] to [{}]", orderId, shippingId, address);
        // simulate email/SMS
    }

    public void notifyCancelled(String orderId, String reason) {
        log.info("❌ Notification: Order [{}] was cancelled. Reason: {}", orderId, reason);
        // simulate email/SMS
    }
}
