package com.example.electronics_store.repository;

import com.example.electronics_store.model.Product;
import com.example.electronics_store.model.User;
import com.example.electronics_store.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {
    List<Wishlist> findByUser(User user);
    
    Optional<Wishlist> findByUserAndProduct(User user, Product product);
    
    boolean existsByUserAndProduct(User user, Product product);
    
    void deleteByUserAndProduct(User user, Product product);
    
    @Query("SELECT COUNT(w) FROM Wishlist w WHERE w.user.id = :userId")
    Long countWishlistItemsByUser(@Param("userId") Integer userId);
    
    @Query("SELECT COUNT(w) FROM Wishlist w WHERE w.product.id = :productId")
    Long countUsersByProduct(@Param("productId") Integer productId);
}
