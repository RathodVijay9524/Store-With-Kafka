package in.vijay.cart_service.controller;
import in.vijay.cart_service.service.CartService;
import in.vijay.dto.order.AddItemToCartRequest;
import in.vijay.dto.order.CartResponse;
import in.vijay.util.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/{userId}")
    public ResponseEntity<?> createCart(
            @PathVariable String userId,
            @RequestBody AddItemToCartRequest request) {
        String cartId = cartService.createCart(userId, request);
        return ExceptionUtil.createBuildResponse(cartId, HttpStatus.CREATED);
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<?> removeItem(@PathVariable String itemId) {
        cartService.removeItemFromCart(itemId);
        return ExceptionUtil.createBuildResponse("Item removed successfully", HttpStatus.OK);
    }

    @DeleteMapping("/clear/{cartId}")
    public ResponseEntity<?> clearCart(@PathVariable String cartId) {
        cartService.clearCart(cartId);
        return ExceptionUtil.createBuildResponse("Cart cleared successfully", HttpStatus.OK);
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<?> getCart(@PathVariable String cartId) {
        CartResponse cart = cartService.getCartById(cartId);
        return ExceptionUtil.createBuildResponse(cart, HttpStatus.OK);
    }

    // ✅ Update quantity of a cart item
    @PutMapping("/item/{itemId}/quantity")
    public ResponseEntity<?> updateCartItemQuantity(
            @PathVariable String itemId,
            @RequestParam int quantity) {
        CartResponse updatedCart = cartService.updateCartItemQuantity(itemId, quantity);
        return ExceptionUtil.createBuildResponse(updatedCart, HttpStatus.OK);
    }

    // ✅ Get cart by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getCartByUserId(@PathVariable String userId) {
        CartResponse cart = cartService.getCartByUserId(userId);
        return ExceptionUtil.createBuildResponse(cart, HttpStatus.OK);
    }

    // ✅ Check if cart exists by user ID
    @GetMapping("/exists/{userId}")
    public ResponseEntity<?> cartExists(@PathVariable String userId) {
        boolean exists = cartService.cartExistsByUserId(userId);
        return ExceptionUtil.createBuildResponse(exists, HttpStatus.OK);
    }

    // ✅ Get all carts
    @GetMapping
    public ResponseEntity<?> getAllCarts() {
        List<CartResponse> carts = cartService.getAllCarts();
        return ExceptionUtil.createBuildResponse(carts, HttpStatus.OK);
    }
}

