package in.vijay.bank.client.service;


import in.vijay.dto.user.UserResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(url = "http://localhost:8081/api/users")
public interface UserHttpClient {
    @GetExchange("/{id}")
    UserResponse getUserById(@PathVariable String id);
}
