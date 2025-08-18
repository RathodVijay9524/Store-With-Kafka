package in.vijay.bank.client.service;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface IdGeneratorClient {

    @GetExchange("/generate")
    String generateId(@RequestParam String entityKey, @RequestParam String prefix, @RequestParam(defaultValue = "6") int width);

    @GetExchange("/generate-date-based")
    String generateDateBasedId(@RequestParam String entityKey, @RequestParam String prefix);

    @GetExchange("/generate-long")
    Long generateLongId(@RequestParam String entityKey);
}

