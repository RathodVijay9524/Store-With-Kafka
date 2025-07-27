package in.vijay.cart_service.controller;
import in.vijay.cart_service.beans.CartItem;
import in.vijay.cart_service.service.CartItemService;
import in.vijay.util.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart-items")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeCartItem(@PathVariable String id) {
        cartItemService.removeCartItem(id);
        return ExceptionUtil.createBuildResponse("Cart item removed successfully", HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCartItem(@PathVariable String id) {
        CartItem item = cartItemService.findById(id);
        // Optionally convert to DTO if needed
        return ExceptionUtil.createBuildResponse(item, HttpStatus.OK);
    }
}

