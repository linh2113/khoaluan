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
            CartDTO cart = cartService.getCartByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success(cart));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<?>> addToCart(
            @RequestParam Integer userId,
            @RequestParam Integer productId,
            @RequestParam Integer quantity) {
            CartDTO cart = cartService.addProductToCart(userId, productId, quantity);

            // Record the event for recommendation system
            // recommendationService.recordUserEvent(userId, productId, "addtocart");

            return ResponseEntity.ok(ApiResponse.success("Thêm thành công sản phẩm vào giỏ hàng", cart));
        
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<?>> updateCartItem(
            @RequestParam Integer userId,
            @RequestParam Integer cartItemId,
            @RequestParam Integer quantity) {
            CartDTO cart = cartService.updateCartItemQuantity(userId, cartItemId, quantity);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật sản phẩm gio hàng thành công", cart));
        
    }

    @DeleteMapping("/remove")
    public ResponseEntity<ApiResponse<?>> removeCartItem(
            @RequestParam Integer userId,
            @RequestParam Integer cartItemId) {
            CartDTO cart = cartService.removeCartItem(userId, cartItemId);
            return ResponseEntity.ok(ApiResponse.success("Xóa sản phẩm khỏi giỏ hàng thành công", cart));
    
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<?>> clearCart(@RequestParam Integer userId) {
            cartService.clearCart(userId);
            return ResponseEntity.ok(ApiResponse.success("Dọn sạch giỏ hàng thành công"));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<?>> getCartItemCount(@RequestParam Integer userId) {
           Long count = cartService.getCartItemCount(userId);
            return ResponseEntity.ok(ApiResponse.success(count));
      
    }

    @GetMapping("/total")
    public ResponseEntity<ApiResponse<?>> getCartTotal(@RequestParam Integer userId) {
            Float total = cartService.calculateCartTotal(userId);
            return ResponseEntity.ok(ApiResponse.success(total));
        
    }
    @PutMapping("/select-items")
    public ResponseEntity<ApiResponse<?>> selectCartItems(
            @RequestParam Integer userId,
            @RequestBody List<Integer> cartItemIds) {
            CartDTO cart = cartService.updateSelectedCartItems(userId, cartItemIds);
            return ResponseEntity.ok(ApiResponse.success("cập nhật thành công sản phẩm trong giỏ hàng", cart));

    }
}
