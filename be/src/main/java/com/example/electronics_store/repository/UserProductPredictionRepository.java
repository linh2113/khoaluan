package com.example.electronics_store.repository;


import com.example.electronics_store.model.Product;
import com.example.electronics_store.model.User;
import com.example.electronics_store.model.UserProductPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserProductPredictionRepository extends JpaRepository<UserProductPrediction, Integer> {
    List<UserProductPrediction> findByUser(User user);

    Page<UserProductPrediction> findByUser(User user, Pageable pageable);

    List<UserProductPrediction> findByProduct(Product product);

    Optional<UserProductPrediction> findByUserAndProduct(User user, Product product);

    @Query("SELECT upp FROM UserProductPrediction upp WHERE upp.user.id = :userId ORDER BY upp.rating DESC")
    List<UserProductPrediction> findTopPredictionsForUser(@Param("userId") Integer userId, Pageable pageable);

    @Query("SELECT COUNT(upp) FROM UserProductPrediction upp WHERE upp.user.id = :userId")
    Long countPredictionsForUser(@Param("userId") Integer userId);

    void deleteByUser(User user);

    void deleteByProduct(Product product);
}
