package com.example.electronics_store.repository;

import com.example.electronics_store.model.Category;
import com.example.electronics_store.model.CategoryDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CategoryDiscountRepository extends JpaRepository<CategoryDiscount, Integer> {
    List<CategoryDiscount> findByCategory(Category category);
    
    @Query("SELECT cd FROM CategoryDiscount cd WHERE cd.category = :category AND cd.discount.isActive = true " +
           "AND cd.discount.startDate <= :now AND cd.discount.endDate >= :now")
    List<CategoryDiscount> findActiveDiscountsByCategory(@Param("category") Category category, @Param("now") LocalDateTime now);
    
    @Query("SELECT cd FROM CategoryDiscount cd WHERE cd.category.id = :categoryId AND cd.discount.isActive = true " +
           "AND cd.discount.startDate <= :now AND cd.discount.endDate >= :now")
    List<CategoryDiscount> findActiveDiscountsByCategoryId(@Param("categoryId") Integer categoryId, @Param("now") LocalDateTime now);
    @Query("SELECT cd FROM CategoryDiscount cd WHERE cd.category.id = :categoryId AND " +
            "cd.discount.startDate <= :orderTime AND cd.discount.endDate >= :orderTime " +
            "AND cd.discount.isActive = true")
    List<CategoryDiscount> findByCategoryAndTimeRange(@Param("categoryId") Integer categoryId,
                                                      @Param("orderTime") LocalDateTime orderTime);
    @Query("SELECT cd FROM CategoryDiscount cd WHERE cd.discount.id = :discountId")
    List<CategoryDiscount> findByDiscountId(@Param("discountId") Integer discountId);

}