package com.example.electronics_store.repository;

import com.example.electronics_store.model.Category;
import com.example.electronics_store.model.CategoryDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryDiscountRepository extends JpaRepository<CategoryDiscount, Integer> {
    List<CategoryDiscount> findByCategory(Category category);
    
    @Query("SELECT cd FROM CategoryDiscount cd WHERE cd.category = :category AND cd.discount.isActive = true " +
           "AND cd.discount.startDate <= :now AND cd.discount.endDate >= :now")
    List<CategoryDiscount> findEffectiveDiscountsByCategory(@Param("category") Category category, @Param("now") LocalDateTime now);
    
    @Query("SELECT cd FROM CategoryDiscount cd WHERE cd.category.id = :categoryId AND cd.discount.isActive = true " +
           "AND cd.discount.startDate <= :now AND cd.discount.endDate >= :now")
    List<CategoryDiscount> findEffectiveDiscountsByCategoryId(@Param("categoryId") Integer categoryId, @Param("now") LocalDateTime now);
    @Query("SELECT cd FROM CategoryDiscount cd WHERE cd.category.id = :categoryId AND " +
            "cd.discount.startDate <= :orderTime AND cd.discount.endDate >= :orderTime " +
            "AND cd.discount.isActive = true")
    List<CategoryDiscount> findByCategoryAndTimeRange(@Param("categoryId") Integer categoryId,
                                                      @Param("orderTime") LocalDateTime orderTime);
    @Query("SELECT cd FROM CategoryDiscount cd WHERE cd.discount.id = :discountId")
    List<CategoryDiscount> findByDiscountId(@Param("discountId") Integer discountId);

    @Query("SELECT cd FROM CategoryDiscount cd WHERE cd.category.id = :categoryId " +
       "AND cd.discount.isActive = true " +
       "AND ((cd.discount.startDate <= :endDate AND cd.discount.endDate >= :startDate))")
    List<CategoryDiscount> findByCategoryAndTimeOverlap(
        @Param("categoryId") Integer categoryId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);
        
    @Query("SELECT cd FROM CategoryDiscount cd WHERE cd.discount.id = :discountId AND cd.category.id = :categoryId")
    Optional<CategoryDiscount> findByDiscountIdAndCategoryId(@Param("discountId") Integer discountId, @Param("categoryId") Integer categoryId);
}