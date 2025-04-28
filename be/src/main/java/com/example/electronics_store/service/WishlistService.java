package com.example.electronics_store.service;

import com.example.electronics_store.dto.WishlistDTO;

import java.util.List;

public interface WishlistService {
    WishlistDTO addToWishlist(Integer userId, Integer productId);
    
    void removeFromWishlist(Integer userId, Integer productId);
    
    List<WishlistDTO> getWishlistByUserId(Integer userId);
    
    boolean isProductInWishlist(Integer userId, Integer productId);
    
    Long countWishlistItemsByUser(Integer userId);
    
    Long countUsersByProduct(Integer productId);
}
