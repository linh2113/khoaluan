package com.example.electronics_store.repository;

import com.example.electronics_store.model.Cart;
import com.example.electronics_store.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    @Query("SELECT DISTINCT c FROM Cart c " +
            "LEFT JOIN FETCH c.cartItems ci " +
            "LEFT JOIN FETCH ci.product " +
            "WHERE c.user = :user")
    Optional<Cart> findByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.cart.user.id = :userId")
    Long countCartItemsByUserId(@Param("userId") Integer userId);
    
    @Query("SELECT SUM(ci.price * ci.quantity) FROM CartItem ci WHERE ci.cart.user.id = :userId")
    Float calculateCartTotalByUserId(@Param("userId") Integer userId);
}
