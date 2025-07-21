package in.vijay.event;

import in.vijay.beans.AuditLog;
import in.vijay.repository.AuditLogRepository;
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
        saveAudit("PASSWORD_CHANGED", String.valueOf(event.getUserId()), event.toString(),event.getEmail());
    }

    @KafkaListener(topics = "account-unlocked", groupId = "audit-group")
    public void onAccountUnlocked(AccountUnlockedEvent event) {
        saveAudit("ACCOUNT_UNLOCKED", String.valueOf(event.getUserId()), event.toString(), event.getEmail());
    }

    private void saveAudit(String action, String userId, String details,String email) {
        AuditLog auditLog  = AuditLog.builder()
                .action(action)
                .userId(userId)
                .details(details)
                .timestamp(LocalDateTime.now())
                .performedBy(email)
                .build();
        repository.save(auditLog);
        log.info("📓 Audit Saved: {}", auditLog );
    }
}

