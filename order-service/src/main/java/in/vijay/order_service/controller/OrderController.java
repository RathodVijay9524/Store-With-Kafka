package in.vijay.order_service.controller;

import in.vijay.dto.order.OrderRequest;
import in.vijay.dto.order.OrderResponse;
import in.vijay.order_service.service.OrderService;
import in.vijay.util.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    // ✅ Create order from cart
    @PostMapping("/place")
    public ResponseEntity<?> placeOrderFromCart(@RequestBody OrderRequest request) {
        log.info("🚀 [API] Request received to place order for user: {}", request.getUserId());
        OrderResponse response = orderService.placeOrderFromCart(request);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.CREATED);
    }
}

