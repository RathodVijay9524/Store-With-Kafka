package in.vijay.event;

import in.vijay.event.category.CategoryCreatedEvent;
import in.vijay.event.category.CategoryDeletedEvent;
import in.vijay.event.category.CategoryUpdatedEvent;
import in.vijay.beans.AuditLog;
import in.vijay.event.product.ProductCreatedEvent;
import in.vijay.event.product.ProductDeletedEvent;
import in.vijay.event.product.ProductUpdatedEvent;
import in.vijay.event.user.*;
import in.vijay.repository.AuditLogRepository;
import in.vijay.service.IdGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditEventListener {

    private final AuditLogRepository repository;
    private final IdGeneratorService idGeneratorService;

    // 🧑‍💼 User Events

    @KafkaListener(topics = "user-created", groupId = "audit-group")
    public void onUserCreated(UserCreatedEvent event) {
        saveAudit("USER_CREATED", String.valueOf(event.getId()), event.toString(), event.getEmail());
    }

    @KafkaListener(topics = "user-updated", groupId = "audit-group")
    public void onUserUpdated(UserUpdatedEvent event) {
        saveAudit("USER_UPDATED", String.valueOf(event.getId()), event.toString(), event.getEmail());
    }

    @KafkaListener(topics = "user-deleted", groupId = "audit-group")
    public void onUserDeleted(UserDeletedEvent event) {
        saveAudit("USER_DELETED", String.valueOf(event.getId()), event.toString(), event.getEmail());
    }

    @KafkaListener(topics = "password-changed", groupId = "audit-group")
    public void onPasswordChanged(PasswordChangedEvent event) {
        saveAudit("PASSWORD_CHANGED", String.valueOf(event.getUserId()), event.toString(), event.getEmail());
    }

    @KafkaListener(topics = "account-unlocked", groupId = "audit-group")
    public void onAccountUnlocked(AccountUnlockedEvent event) {
        saveAudit("ACCOUNT_UNLOCKED", String.valueOf(event.getUserId()), event.toString(), event.getEmail());
    }

    // 🗂️ Category Events

    @KafkaListener(topics = "category-created", groupId = "audit-group")
    public void onCategoryCreated(CategoryCreatedEvent event) {
        saveAudit("CATEGORY_CREATED", String.valueOf(event.getId()), event.toString(), "system");
    }

    @KafkaListener(topics = "category-updated", groupId = "audit-group")
    public void onCategoryUpdated(CategoryUpdatedEvent event) {
        saveAudit("CATEGORY_UPDATED", String.valueOf(event.getId()), event.toString(), "system");
    }

    @KafkaListener(topics = "category-deleted", groupId = "audit-group")
    public void onCategoryDeleted(CategoryDeletedEvent event) {
        saveAudit("CATEGORY_DELETED", String.valueOf(event.getId()), event.toString(), "system");
    }

    // ✅ PRODUCT EVENTS

    @KafkaListener(topics = "product-created-topic", groupId = "audit-group")
    public void onProductCreated(ProductCreatedEvent event) {
        saveAudit("PRODUCT_CREATED", event.getProductId(), event.toString(), "system");
    }

    @KafkaListener(topics = "product-updated-topic", groupId = "audit-group")
    public void onProductUpdated(ProductUpdatedEvent event) {
        saveAudit("PRODUCT_UPDATED", event.getProductId(), event.toString(), "system");
    }

    @KafkaListener(topics = "product-deleted-topic", groupId = "audit-group")
    public void onProductDeleted(ProductDeletedEvent event) {
        saveAudit("PRODUCT_DELETED", event.getProductId(), event.toString(), "system");
    }


    private void saveAudit(String action, String entityId, String details, String performedBy) {
        String auditId = idGeneratorService.generateDateBasedId("AUDIT", "AUD"); // e.g., AUD-000001
        AuditLog auditLog = AuditLog.builder()
                .id(auditId) // ✅ Set formatted ID
                .action(action)
                .userId(entityId)
                .details(details)
                .timestamp(LocalDateTime.now())
                .performedBy(performedBy)
                .build();

        repository.save(auditLog);
        log.info("📓 Audit Saved: {}", auditLog);
    }

}


