package com.example.electronics_store.repository;

import com.example.electronics_store.model.Cart;
import com.example.electronics_store.model.CartItem;
import com.example.electronics_store.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findByCart(Cart cart);
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
    void deleteAllByCartId(@Param("cartId") Integer cartId);
    @Modifying
    @Query("UPDATE CartItem ci SET ci.selected = true WHERE ci.id IN :ids AND ci.cart.id = :cartId")
    void selectCartItems(@Param("ids") List<Integer> ids, @Param("cartId") Integer cartId);
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.id IN :ids")
    void deleteAllByIds(@Param("ids") List<Integer> ids);
    @Modifying
    @Query("UPDATE CartItem ci SET ci.selected = false WHERE ci.id NOT IN :ids AND ci.cart.id = :cartId")
    void deselectOtherCartItems(@Param("ids") List<Integer> ids, @Param("cartId") Integer cartId);
    List<CartItem> findByCartAndSelected(Cart cart, Boolean selected);
    @Modifying
    @Query("UPDATE CartItem ci SET ci.quantity = :quantity WHERE ci.id = :id")
    void updateQuantity(@Param("id") Integer id, @Param("quantity") Integer quantity);
}
