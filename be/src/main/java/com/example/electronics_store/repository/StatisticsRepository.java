package com.example.electronics_store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.electronics_store.model.Order;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatisticsRepository extends JpaRepository<Order, Integer> {

    /**
     * Lấy doanh thu theo ngày trong khoảng thời gian
     */
    @Query(value = "SELECT DATE(o.create_at) as order_date, SUM(o.total_price) as total_sales " +
            "FROM orders o " +
            "WHERE o.create_at BETWEEN TO_TIMESTAMP(:startDate, 'YYYY-MM-DD') AND TO_TIMESTAMP(:endDate, 'YYYY-MM-DD') AND o.order_status = 4 " +
            "GROUP BY DATE(o.create_at) " +
            "ORDER BY order_date", nativeQuery = true)
    List<Object[]> getRevenueByDay(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * Lấy doanh thu theo tuần trong khoảng thời gian
     */
    @Query(value = "SELECT TO_CHAR(o.create_at, 'YYYY-IW') as week, SUM(o.total_price) as total_sales " +
            "FROM orders o " +
            "WHERE o.create_at BETWEEN TO_TIMESTAMP(:startDate, 'YYYY-MM-DD') AND TO_TIMESTAMP(:endDate, 'YYYY-MM-DD') AND o.order_status = 4 " +
            "GROUP BY TO_CHAR(o.create_at, 'YYYY-IW') " +
            "ORDER BY week", nativeQuery = true)
    List<Object[]> getRevenueByWeek(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * Lấy doanh thu theo tháng trong khoảng thời gian
     */
    @Query(value = "SELECT TO_CHAR(o.create_at, 'YYYY-MM') as month, SUM(o.total_price) as total_sales " +
            "FROM orders o " +
            "WHERE o.create_at BETWEEN TO_TIMESTAMP(:startDate, 'YYYY-MM-DD') AND TO_TIMESTAMP(:endDate, 'YYYY-MM-DD') AND o.order_status = 4 " +
            "GROUP BY TO_CHAR(o.create_at, 'YYYY-MM') " +
            "ORDER BY month", nativeQuery = true)
    List<Object[]> getRevenueByMonth(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * Lấy doanh thu theo năm trong khoảng thời gian
     */
    @Query(value = "SELECT TO_CHAR(o.create_at, 'YYYY') as year, SUM(o.total_price) as total_sales " +
            "FROM orders o " +
            "WHERE o.create_at BETWEEN TO_TIMESTAMP(:startDate, 'YYYY-MM-DD') AND TO_TIMESTAMP(:endDate, 'YYYY-MM-DD') AND o.order_status = 4 " +
            "GROUP BY TO_CHAR(o.create_at, 'YYYY') " +
            "ORDER BY year", nativeQuery = true)
    List<Object[]> getRevenueByYear(@Param("startDate") String startDate, @Param("endDate") String endDate);


    /**
     * Lấy doanh thu theo danh mục sản phẩm
     */
    @Query(value = "SELECT c.category_name, SUM(od.price * od.quantity) as total_revenue " +
            "FROM order_details od " +
            "JOIN orders o ON od.id_order = o.id " +
            "JOIN products p ON od.id_product = p.id " +
            "JOIN categories c ON p.id_category = c.id " +
            "WHERE o.order_status = 4 " +
            "AND (:startDate IS NULL OR o.create_at >= CAST(:startDate AS timestamp)) " +
            "AND (:endDate IS NULL OR o.create_at <= CAST(:endDate AS timestamp)) " +
            "GROUP BY c.category_name " +
            "ORDER BY total_revenue DESC", nativeQuery = true)
    List<Object[]> getRevenueByCategoryPieChart(@Param("startDate") String startDate, @Param("endDate") String endDate);


}