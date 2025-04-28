package com.example.electronics_store.controller;

import com.example.electronics_store.dto.ApiResponse;
import com.example.electronics_store.dto.WishlistDTO;
import com.example.electronics_store.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wishlist")
@CrossOrigin(origins = "*")
public class WishlistController {

    private final WishlistService wishlistService;

    @Autowired
    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getWishlist(@RequestParam Integer userId) {
        try {
            List<WishlistDTO> wishlist = wishlistService.getWishlistByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success(wishlist));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<?>> addToWishlist(
            @RequestParam Integer userId,
            @RequestParam Integer productId) {
        try {
            WishlistDTO wishlistItem = wishlistService.addToWishlist(userId, productId);
            return ResponseEntity.ok(ApiResponse.success("Product added to wishlist", wishlistItem));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<ApiResponse<?>> removeFromWishlist(
            @RequestParam Integer userId,
            @RequestParam Integer productId) {
        try {
            wishlistService.removeFromWishlist(userId, productId);
            return ResponseEntity.ok(ApiResponse.success("Product removed from wishlist"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/check")
    public ResponseEntity<ApiResponse<?>> checkIfProductInWishlist(
            @RequestParam Integer userId,
            @RequestParam Integer productId) {
        try {
            boolean isInWishlist = wishlistService.isProductInWishlist(userId, productId);
            return ResponseEntity.ok(ApiResponse.success(isInWishlist));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<?>> getWishlistItemCount(@RequestParam Integer userId) {
        try {
            Long count = wishlistService.countWishlistItemsByUser(userId);
            return ResponseEntity.ok(ApiResponse.success(count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
