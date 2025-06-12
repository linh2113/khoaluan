package com.example.electronics_store.repository;

import com.example.electronics_store.model.Order;
import com.example.electronics_store.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer>, JpaSpecificationExecutor<Order> {
    List<Order> findByUser(User user);
    
    List<Order> findByOrderStatus(Integer orderStatus);
    
    List<Order> findByPaymentStatus(String paymentStatus);
    
    @Query("SELECT o FROM Order o WHERE o.createAt BETWEEN :startDate AND :endDate")
    List<Order> findOrdersInDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.createAt BETWEEN :startDate AND :endDate AND o.orderStatus = 4")
    Float calculateTotalSalesInDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.user = :user")
    Long countOrdersByUser(@Param("user") User user);
    
    @Query(value = "SELECT DATE(o.create_at) as order_date, COUNT(*) as order_count " +
            "FROM orders o " +
            "WHERE o.create_at BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(o.create_at) " +
            "ORDER BY order_date", nativeQuery = true)
    List<Object[]> getOrderCountByDay(@Param("startDate") String startDate, @Param("endDate") String endDate);
    
    @Query(value = "SELECT DATE(o.create_at) as order_date, SUM(o.total_price) as total_sales " +
            "FROM orders o " +
            "WHERE o.create_at BETWEEN :startDate AND :endDate AND o.order_status = 4 " +
            "GROUP BY DATE(o.create_at) " +
            "ORDER BY order_date", nativeQuery = true)
    List<Object[]> getSalesByDay(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("SELECT o FROM Order o WHERE o.orderStatus = 3 AND o.updatedAt <= :cutoffDate")
    List<Order> findOrdersToAutoComplete(@Param("cutoffDate") LocalDateTime cutoffDate);

}
