package in.vijay.order_service.client.service;

import in.vijay.dto.ApiResponse;
import in.vijay.dto.order.CartResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface CartHttpClient {

    @GetExchange("/user/{userId}")
    ApiResponse<CartResponse> getCartByUserId(@PathVariable String userId);

    @DeleteExchange("/clear/{cartId}")
    void clearCart(@PathVariable String cartId);
}
