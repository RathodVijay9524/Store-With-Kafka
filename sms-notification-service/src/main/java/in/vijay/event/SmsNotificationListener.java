package in.vijay.event;


import in.vijay.event.user.UserCreatedEvent;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import in.vijay.config.TwilioProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsNotificationListener {

    private final TwilioProperties twilioProperties;

    @KafkaListener(topics = "user-created", groupId = "sms-group")
    public void handleUserCreated(UserCreatedEvent event) {
        String message = String.format("Hi %s, welcome to our platform!", event.getFirstName());
        sendSms(event.getPhoneNumber(), message);
    }

    private void sendSms(String to, String body) {
        try {
            Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(twilioProperties.getFromNumber()),
                    body
            ).create();
            log.info("✅ SMS sent to {}", to);
        } catch (Exception e) {
            log.error("❌ Failed to send SMS to {}: {}", to, e.getMessage());
        }
    }
}

