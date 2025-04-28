package com.example.electronics_store.service.impl;

import com.example.electronics_store.dto.WishlistDTO;
import com.example.electronics_store.model.Product;
import com.example.electronics_store.model.User;
import com.example.electronics_store.model.Wishlist;
import com.example.electronics_store.repository.ProductRepository;
import com.example.electronics_store.repository.UserRepository;
import com.example.electronics_store.repository.WishlistRepository;
import com.example.electronics_store.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Autowired
    public WishlistServiceImpl(
            WishlistRepository wishlistRepository,
            UserRepository userRepository,
            ProductRepository productRepository) {
        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public WishlistDTO addToWishlist(Integer userId, Integer productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if product is already in wishlist
        if (wishlistRepository.existsByUserAndProduct(user, product)) {
            throw new RuntimeException("Product already in wishlist");
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setProduct(product);

        Wishlist savedWishlist = wishlistRepository.save(wishlist);
        return mapWishlistToDTO(savedWishlist);
    }

    @Override
    @Transactional
    public void removeFromWishlist(Integer userId, Integer productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if product is in wishlist
        if (!wishlistRepository.existsByUserAndProduct(user, product)) {
            throw new RuntimeException("Product not in wishlist");
        }

        wishlistRepository.deleteByUserAndProduct(user, product);
    }

    @Override
    public List<WishlistDTO> getWishlistByUserId(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return wishlistRepository.findByUser(user).stream()
                .map(this::mapWishlistToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isProductInWishlist(Integer userId, Integer productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return wishlistRepository.existsByUserAndProduct(user, product);
    }

    @Override
    public Long countWishlistItemsByUser(Integer userId) {
        return wishlistRepository.countWishlistItemsByUser(userId);
    }

    @Override
    public Long countUsersByProduct(Integer productId) {
        return wishlistRepository.countUsersByProduct(productId);
    }

    // Helper method
    private WishlistDTO mapWishlistToDTO(Wishlist wishlist) {
        return WishlistDTO.builder()
                .id(wishlist.getId())
                .userId(wishlist.getUser().getId())
                .productId(wishlist.getProduct().getId())
                .productName(wishlist.getProduct().getName())
                .productImage(wishlist.getProduct().getImage())
                .productPrice(wishlist.getProduct().getPrice())
                .addedAt(wishlist.getAddedAt())
                .build();
    }
}
