package com.example.electronics_store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private Long totalUsers;
    private Long totalProducts;
    private Long totalOrders;
    private Float totalRevenue;
    
    private Long newUsersToday;
    private Long newOrdersToday;
    private Float revenueToday;
    
    private Long pendingOrders;
    private Long processingOrders;
    private Long shippedOrders;
    private Long deliveredOrders;
    private Long cancelledOrders;
    
    private Long lowStockProducts;
    
    private List<Map<String, Object>> recentOrders;
    private List<Map<String, Object>> topSellingProducts;
    private List<Map<String, Object>> topCustomers;
    
    private Map<String, Object> salesByCategory;
    private Map<String, Object> revenueByMonth;
}
