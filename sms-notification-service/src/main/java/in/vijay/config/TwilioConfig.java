package in.vijay.config;

import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class TwilioConfig {

    private final TwilioProperties properties;

    @PostConstruct
    public void initTwilio() {
        Twilio.init(properties.getAccountSid(), properties.getAuthToken());
    }
}

