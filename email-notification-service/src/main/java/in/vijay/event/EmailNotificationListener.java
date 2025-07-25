package in.vijay.event;

import in.vijay.event.user.UserCreatedEvent;
import in.vijay.event.user.UserDeletedEvent;
import in.vijay.event.user.UserUpdatedEvent;
import in.vijay.util.EmailUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class EmailNotificationListener {

    private final EmailUtils emailUtils;

    @KafkaListener(topics = "user-created", groupId = "email-group")
    public void handleUserCreated(UserCreatedEvent event) {
        String to = event.getEmail();
        String subject = "Welcome to our platform";
        String body = "<h3>Hi " + event.getFirstName() + ",</h3><p>Welcome to our application!</p>";

        boolean sent = emailUtils.sendEmail(to, subject, body);
        if (sent) {
            log.info("📧 Email sent to new user: {}", to);
        } else {
            log.error("❌ Failed to send welcome email to: {}", to);
        }
    }

    @KafkaListener(topics = "user-updated", groupId = "email-group")
    public void handleUserUpdated(UserUpdatedEvent event) {
        String to = event.getEmail();
        String subject = "Your profile has been updated";
        String body = "<p>Dear " + event.getFirstName() + ",</p><p>Your user profile was successfully updated.</p>";

        boolean sent = emailUtils.sendEmail(to, subject, body);
        if (sent) {
            log.info("📧 Profile update email sent to: {}", to);
        } else {
            log.error("❌ Failed to send profile update email to: {}", to);
        }
    }

    @KafkaListener(topics = "user-deleted", groupId = "email-group")
    public void handleUserDeleted(UserDeletedEvent event) {
        String to = event.getEmail();
        String subject = "Sorry to see you go!";
        String body = "<p>Hi,</p><p>Your account has been deleted. We’re sorry to see you leave.</p>";

        boolean sent = emailUtils.sendEmail(to, subject, body);
        if (sent) {
            log.info("📧 Goodbye email sent to: {}", to);
        } else {
            log.error("❌ Failed to send goodbye email to: {}", to);
        }
    }
}


   /* @KafkaListener(topics = "user-created", groupId = "email-group")
    public void handleUserCreated(UserCreatedEvent event) {
        log.info("📧 Email Sent: Welcome {}", event.getFirstName());
    }

    @KafkaListener(topics = "user-updated", groupId = "email-group")
    public void handleUserUpdated(UserUpdatedEvent event) {
        log.info("📧 Email Sent: Your profile was updated, {}", event.getFirstName());
    }

    @KafkaListener(topics = "user-deleted", groupId = "email-group")
    public void handleUserDeleted(UserDeletedEvent event) {
        log.info("📧 Email Sent: Sorry to see you go, {}", event.getEmail());
    }
}*/

