package com.example.electronics_store.service;

import com.example.electronics_store.dto.CartDTO;
import com.example.electronics_store.model.Cart;

import java.util.List;
import java.util.Optional;

public interface CartService {
    CartDTO getCartByUserId(Integer userId);
    
    CartDTO addProductToCart(Integer userId, Integer productId, Integer quantity);
    CartDTO updateSelectedCartItems(Integer userId, List<Integer> selectedCartItemIds);
    CartDTO updateCartItemQuantity(Integer userId, Integer cartItemId, Integer quantity);
    
    CartDTO removeCartItem(Integer userId, Integer cartItemId);
    
    void clearCart(Integer userId);
    
    Long getCartItemCount(Integer userId);
    
    Float calculateCartTotal(Integer userId);
    
    Optional<Cart> getCartEntityByUserId(Integer userId);
}
