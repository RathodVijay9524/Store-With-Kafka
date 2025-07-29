package in.vijay.order_service.controller;

import in.vijay.dto.order.OrderItemRequest;
import in.vijay.dto.order.OrderItemResponse;
import in.vijay.order_service.service.OrderItemService;
import in.vijay.util.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    // ✅ Create Order Item
    @PostMapping("/{orderId}")
    public ResponseEntity<?> createOrderItem(
            @PathVariable String orderId,
            @RequestBody OrderItemRequest requestDto
    ) {
        OrderItemResponse responseDto = orderItemService.addOrderItem(orderId, requestDto);
        return ExceptionUtil.createBuildResponse(responseDto, HttpStatus.CREATED);
    }

    // ✅ Get Single Order Item
    @GetMapping("/{itemId}")
    public ResponseEntity<?> getOrderItemById(@PathVariable String itemId) {
        OrderItemResponse responseDto = orderItemService.getOrderItemById(itemId);
        return ExceptionUtil.createBuildResponse(responseDto, HttpStatus.OK);
    }

    // ✅ Get All Items for Order
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getAllItemsForOrder(@PathVariable String orderId) {
        List<OrderItemResponse> items = orderItemService.getAllItemsForOrder(orderId);
        return ExceptionUtil.createBuildResponse(items, HttpStatus.OK);
    }

    // ✅ Update Order Item
    @PutMapping("/{itemId}")
    public ResponseEntity<?> updateOrderItem(
            @PathVariable String itemId,
            @RequestBody OrderItemRequest requestDto
    ) {
        OrderItemResponse responseDto = orderItemService.updateOrderItem(itemId, requestDto);
        return ExceptionUtil.createBuildResponse(responseDto, HttpStatus.OK);
    }

    // ✅ Delete Order Item
    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> deleteOrderItem(@PathVariable String itemId) {
        orderItemService.deleteOrderItem(itemId);
        return ExceptionUtil.createBuildResponse("Order item deleted successfully!", HttpStatus.OK);
    }
}
