package com.example.electronics_store.repository;

import com.example.electronics_store.model.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    List<OrderDetail> findByOrder(Order order);

    List<OrderDetail> findByProduct(Product product);

    List<OrderDetail> findByOrderAndReviewStatus(Order order, Boolean reviewStatus);

    @Query("SELECT SUM(od.quantity) FROM OrderDetail od WHERE od.product.id = :productId")
    Integer getTotalSoldQuantityByProduct(@Param("productId") Integer productId);

  @Query("SELECT od.product.id, SUM(od.quantity) as soldQuantity " +
       "FROM OrderDetail od " +
       "WHERE od.order.orderStatus = 4 " +
       "GROUP BY od.product.id " +
       "ORDER BY soldQuantity DESC")
List<Object[]> findTopSellingProducts(Pageable pageable);


    
    @Query("SELECT COUNT(od) > 0 FROM OrderDetail od " +
           "JOIN od.order o " +
           "WHERE od.product.id = :productId " +
           "AND o.user.id = :userId " +
           "AND o.orderStatus = 4") 
    boolean existsByProductIdAndUserIdAndOrderCompleted(
            @Param("productId") Integer productId, 
            @Param("userId") Integer userId);


    @Modifying
    @Query("UPDATE OrderDetail od SET od.reviewStatus = :status " +
           "WHERE od.product.id = :productId " +
           "AND od.order.user.id = :userId " +
           "AND od.order.orderStatus = 4") // 4 = Completed
    void updateReviewStatusForProductAndUser(
            @Param("productId") Integer productId, 
            @Param("userId") Integer userId, 
            @Param("status") boolean status);        

}
