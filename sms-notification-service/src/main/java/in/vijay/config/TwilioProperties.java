package in.vijay.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "twilio")
@Data
public class TwilioProperties {
    private String accountSid;
    private String authToken;
    private String fromNumber;
}

