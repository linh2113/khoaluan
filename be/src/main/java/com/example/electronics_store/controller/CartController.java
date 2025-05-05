package com.example.electronics_store.controller;

import com.example.electronics_store.dto.ApiResponse;
import com.example.electronics_store.dto.CartDTO;
import com.example.electronics_store.service.CartService;
// import com.example.electronics_store.service.ProductRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;
    // private final ProductRecommendationService recommendationService;

    @Autowired
    public CartController(CartService cartService
                         /* ProductRecommendationService recommendationService */) {
        this.cartService = cartService;
        // this.recommendationService = recommendationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getCart(@RequestParam Integer userId) {
        try {
            CartDTO cart = cartService.getCartByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success(cart));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<?>> addToCart(
            @RequestParam Integer userId,
            @RequestParam Integer productId,
            @RequestParam Integer quantity) {
        try {
            CartDTO cart = cartService.addProductToCart(userId, productId, quantity);

            // Record the event for recommendation system
            // recommendationService.recordUserEvent(userId, productId, "addtocart");

            return ResponseEntity.ok(ApiResponse.success("Product added to cart", cart));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<?>> updateCartItem(
            @RequestParam Integer userId,
            @RequestParam Integer cartItemId,
            @RequestParam Integer quantity) {
        try {
            CartDTO cart = cartService.updateCartItemQuantity(userId, cartItemId, quantity);
            return ResponseEntity.ok(ApiResponse.success("Cart item updated", cart));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<ApiResponse<?>> removeCartItem(
            @RequestParam Integer userId,
            @RequestParam Integer cartItemId) {
        try {
            CartDTO cart = cartService.removeCartItem(userId, cartItemId);
            return ResponseEntity.ok(ApiResponse.success("Cart item removed", cart));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<?>> clearCart(@RequestParam Integer userId) {
        try {
            cartService.clearCart(userId);
            return ResponseEntity.ok(ApiResponse.success("Cart cleared"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<?>> getCartItemCount(@RequestParam Integer userId) {
        try {
            Long count = cartService.getCartItemCount(userId);
            return ResponseEntity.ok(ApiResponse.success(count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/total")
    public ResponseEntity<ApiResponse<?>> getCartTotal(@RequestParam Integer userId) {
        try {
            Float total = cartService.calculateCartTotal(userId);
            return ResponseEntity.ok(ApiResponse.success(total));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    @PutMapping("/select-items")
    public ResponseEntity<ApiResponse<?>> selectCartItems(
            @RequestParam Integer userId,
            @RequestBody List<Integer> cartItemIds) {
        try {
            CartDTO cart = cartService.updateSelectedCartItems(userId, cartItemIds);
            return ResponseEntity.ok(ApiResponse.success("Cart items selection updated", cart));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
