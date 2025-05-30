package com.example.electronics_store.repository;

import com.example.electronics_store.model.Product;
import com.example.electronics_store.model.ProductDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductDiscountRepository extends JpaRepository<ProductDiscount, Integer> {
    List<ProductDiscount> findByProduct(Product product);
    
    @Query("SELECT pd FROM ProductDiscount pd WHERE pd.product = :product AND pd.discount.isActive = true " +
           "AND pd.discount.startDate <= :now AND pd.discount.endDate >= :now " +
           "ORDER BY pd.discount.priority DESC")
    List<ProductDiscount> findActiveDiscountsByProduct(@Param("product") Product product, @Param("now") LocalDateTime now);
    
    @Query("SELECT pd FROM ProductDiscount pd WHERE pd.product.id = :productId AND pd.discount.isActive = true " +
           "AND pd.discount.startDate <= :now AND pd.discount.endDate >= :now " +
           "ORDER BY pd.discount.priority DESC")
    List<ProductDiscount> findActiveDiscountsByProductId(@Param("productId") Integer productId, @Param("now") LocalDateTime now);
    @Query("SELECT pd FROM ProductDiscount pd WHERE pd.product.id = :productId AND " +
            "pd.discount.startDate <= :orderTime AND pd.discount.endDate >= :orderTime " +
            "AND pd.discount.isActive = true")
    List<ProductDiscount> findByProductAndTimeRange(@Param("productId") Integer productId,
                                                    @Param("orderTime") LocalDateTime orderTime);
    @Query("SELECT pd FROM ProductDiscount pd WHERE pd.discount.id = :discountId")
    List<ProductDiscount> findByDiscountId(@Param("discountId") Integer discountId);

}