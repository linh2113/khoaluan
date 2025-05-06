package com.example.electronics_store.repository;

import com.example.electronics_store.model.Product;
import com.example.electronics_store.model.Rating;
import com.example.electronics_store.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {
    List<Rating> findByProduct(Product product);
    
    List<Rating> findByUser(User user);
    
    Optional<Rating> findByProductAndUser(Product product, User user);
    
    List<Rating> findByParent(Rating parent);

    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.product.id = :productId AND r.parent IS NULL AND r.rating IS NOT NULL")
    Double getAverageRatingForProduct(@Param("productId") Integer productId);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.product.id = :productId AND r.parent IS NULL AND r.rating IS NOT NULL")
    Long countRatingsByProduct(@Param("productId") Integer productId);

    @Query("SELECT r.rating, COUNT(r) FROM Rating r WHERE r.product.id = :productId AND r.parent IS NULL AND r.rating IS NOT NULL GROUP BY r.rating ORDER BY r.rating DESC")
    List<Object[]> getRatingDistributionForProduct(@Param("productId") Integer productId);
    
    @Query("SELECT r FROM Rating r WHERE r.product.id = :productId AND r.parent IS NULL ORDER BY r.createAt DESC")
    List<Rating> findTopLevelRatingsByProduct(@Param("productId") Integer productId);
}
