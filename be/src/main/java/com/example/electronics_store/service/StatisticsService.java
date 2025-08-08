package com.example.electronics_store.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface StatisticsService {
    Map<String, Object> getDashboardStatistics();
    
    Map<String, Object> getSalesStatistics(LocalDateTime startDate, LocalDateTime endDate);
    
    Map<String, Object> getOrderStatistics(LocalDateTime startDate, LocalDateTime endDate);
    
    Map<String, Object> getProductStatistics();
    
    Map<String, Object> getUserStatistics();
    
    List<Map<String, Object>> getTopSellingProducts(Integer limit);
    
    List<Map<String, Object>> getTopCustomers(Integer limit);
    
    Map<String, Object> getSalesByCategory();
    
    Map<String, Object> getSalesByPaymentMethod();
    
    Map<String, Object> getSalesByShippingMethod();
    
    Map<String, Object> getRevenueByMonth(Integer year);

    Map<String, Object> getRevenueByDay(LocalDateTime startDate, LocalDateTime endDate);
    Map<String, Object> getRevenueByTimeInterval(LocalDateTime startDate, LocalDateTime endDate, String interval);
    Map<String, Object> getRevenueByCategoryPieChart(LocalDateTime startDate, LocalDateTime endDate);
}
