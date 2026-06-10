package com.example.electronics_store.service.impl;

import com.example.electronics_store.model.Order;
import com.example.electronics_store.model.Product;
import com.example.electronics_store.repository.*;
import com.example.electronics_store.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final StatisticsRepository statisticsRepository;

    @Autowired
    public StatisticsServiceImpl(
            OrderRepository orderRepository,
            OrderDetailRepository orderDetailRepository,
            UserRepository userRepository,
            ProductRepository productRepository,
            StatisticsRepository statisticsRepository
           ) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.statisticsRepository = statisticsRepository;
    }

    @Override
public Map<String, Object> getDashboardStatistics() {
    LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
    
    // Parallel execution for independent queries
    CompletableFuture<Long> totalUsersFuture = CompletableFuture.supplyAsync(() -> userRepository.count());
    CompletableFuture<Long> totalProductsFuture = CompletableFuture.supplyAsync(() -> productRepository.count());
    CompletableFuture<Long> totalOrdersFuture = CompletableFuture.supplyAsync(() -> orderRepository.count());
    
    CompletableFuture<Float> totalRevenueFuture = CompletableFuture.supplyAsync(() -> 
        orderRepository.calculateTotalSalesInDateRange(LocalDateTime.now().minusYears(100), LocalDateTime.now()));
    
    CompletableFuture<Long> newUsersTodayFuture = CompletableFuture.supplyAsync(() -> 
        userRepository.countNewUsersToday(startOfDay));
    
    CompletableFuture<Long> newOrdersTodayFuture = CompletableFuture.supplyAsync(() -> 
        orderRepository.countNewOrdersToday(startOfDay));
    
    CompletableFuture<Float> revenueTodayFuture = CompletableFuture.supplyAsync(() -> 
        orderRepository.calculateTotalSalesInDateRange(startOfDay, LocalDateTime.now()));
    
    CompletableFuture<List<Object[]>> orderStatusFuture = CompletableFuture.supplyAsync(() -> 
        orderRepository.countOrdersByStatus());
    
    CompletableFuture<Long> lowStockFuture = CompletableFuture.supplyAsync(() -> 
        (long) productRepository.findLowStockProducts().size());
    
    CompletableFuture<List<Object[]>> recentOrdersFuture = CompletableFuture.supplyAsync(() -> 
        orderRepository.findRecentOrders());
    
    CompletableFuture<List<Object[]>> topProductsFuture = CompletableFuture.supplyAsync(() -> 
        orderDetailRepository.findTopSellingProductsForDashboard());
    
    CompletableFuture<List<Object[]>> topCustomersFuture = CompletableFuture.supplyAsync(() -> 
        userRepository.findTopCustomers());
    
    try {
        // Wait for all futures to complete
        CompletableFuture.allOf(totalUsersFuture, totalProductsFuture, totalOrdersFuture,
                totalRevenueFuture, newUsersTodayFuture, newOrdersTodayFuture,
                revenueTodayFuture, orderStatusFuture, lowStockFuture,
                recentOrdersFuture, topProductsFuture, topCustomersFuture).join();
        
        // Get results
        long totalUsers = totalUsersFuture.get();
        long totalProducts = totalProductsFuture.get();
        long totalOrders = totalOrdersFuture.get();
        Float totalRevenue = totalRevenueFuture.get();
        long newUsersToday = newUsersTodayFuture.get();
        long newOrdersToday = newOrdersTodayFuture.get();
        Float revenueToday = revenueTodayFuture.get();
        
        // Process order status counts
        Map<Integer, Long> statusCounts = new HashMap<>();
        for (Object[] row : orderStatusFuture.get()) {
            statusCounts.put((Integer) row[0], (Long) row[1]);
        }
        
        // Process recent orders
        List<Map<String, Object>> recentOrders = recentOrdersFuture.get().stream()
                .map(row -> {
                    Map<String, Object> orderMap = new HashMap<>();
                    orderMap.put("id", row[0]);
                    orderMap.put("userId", row[1]);
                    orderMap.put("userName", row[2]);
                    orderMap.put("totalPrice", row[3]);
                    orderMap.put("createAt", row[4]);
                    orderMap.put("orderStatus", row[5]);
                    return orderMap;
                })
                .collect(Collectors.toList());
        
        // Process top selling products
        List<Map<String, Object>> topSellingProducts = topProductsFuture.get().stream()
                .map(row -> {
                    Map<String, Object> productMap = new HashMap<>();
                    productMap.put("id", row[0]);
                    productMap.put("name", row[1]);
                    productMap.put("price", row[2]);
                    productMap.put("soldQuantity", row[3]);
                    return productMap;
                })
                .collect(Collectors.toList());
        
        // Process top customers
        List<Map<String, Object>> topCustomers = topCustomersFuture.get().stream()
                .map(row -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", row[0]);
                    userMap.put("userName", row[1]);
                    userMap.put("email", row[2]);
                    userMap.put("role", "Customer");
                    userMap.put("orderCount", row[3]);
                    return userMap;
                })
                .collect(Collectors.toList());
        
        // Build response directly as Map
        Map<String, Object> result = new HashMap<>();
        result.put("totalUsers", totalUsers);
        result.put("totalProducts", totalProducts);
        result.put("totalOrders", totalOrders);
        result.put("totalRevenue", totalRevenue != null ? totalRevenue : 0f);
        result.put("newUsersToday", newUsersToday);
        result.put("newOrdersToday", newOrdersToday);
        result.put("revenueToday", revenueToday != null ? revenueToday : 0f);
        result.put("pendingOrders", statusCounts.getOrDefault(0, 0L));
        result.put("processingOrders", statusCounts.getOrDefault(1, 0L));
        result.put("shippedOrders", statusCounts.getOrDefault(2, 0L));
        result.put("deliveredOrders", statusCounts.getOrDefault(3, 0L));
        result.put("completedOrders", statusCounts.getOrDefault(4, 0L));
        result.put("cancelledOrders", statusCounts.getOrDefault(5, 0L));
        result.put("lowStockProducts", lowStockFuture.get());
        result.put("recentOrders", recentOrders);
        result.put("topSellingProducts", topSellingProducts);
        result.put("topCustomers", topCustomers);
        
        return result;
        
    } catch (Exception e) {
        throw new RuntimeException("Error getting dashboard statistics", e);
    }
}

    @Override
    public Map<String, Object> getSalesStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        // Get total sales in date range
        Float totalSales = orderRepository.calculateTotalSalesInDateRange(startDate, endDate);
        
        // Get sales by day
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startDateStr = startDate.format(formatter);
        String endDateStr = endDate.format(formatter);
        
        List<Object[]> salesByDay = orderRepository.getSalesByDay(startDateStr, endDateStr);
        Map<String, Float> salesByDayMap = new LinkedHashMap<>();
        
        for (Object[] row : salesByDay) {
            String date = row[0].toString();
            Float sales = ((Number) row[1]).floatValue();
            salesByDayMap.put(date, sales);
        }
        
        // Build the response
        Map<String, Object> result = new HashMap<>();
        result.put("totalSales", totalSales != null ? totalSales : 0f);
        result.put("salesByDay", salesByDayMap);
        
        return result;
    }

    @Override
    public Map<String, Object> getOrderStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        // Get order count in date range
        List<Order> orders = orderRepository.findOrdersInDateRange(startDate, endDate);
        long totalOrders = orders.size();
        
        // Get order count by status
        Map<Integer, Long> ordersByStatus = orders.stream()
                .collect(Collectors.groupingBy(Order::getOrderStatus, Collectors.counting()));
        
        // Get order count by day
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startDateStr = startDate.format(formatter);
        String endDateStr = endDate.format(formatter);
        
        List<Object[]> orderCountByDay = orderRepository.getOrderCountByDay(startDateStr, endDateStr);
        Map<String, Long> orderCountByDayMap = new LinkedHashMap<>();
        
        for (Object[] row : orderCountByDay) {
            String date = row[0].toString();
            Long count = ((Number) row[1]).longValue();
            orderCountByDayMap.put(date, count);
        }
        
        // Build the response
        Map<String, Object> result = new HashMap<>();
        result.put("totalOrders", totalOrders);
        result.put("ordersByStatus", ordersByStatus);
        result.put("orderCountByDay", orderCountByDayMap);
        
        return result;
    }

    @Override
    public Map<String, Object> getProductStatistics() {
        // Get total products
        long totalProducts = productRepository.count();
        
        // Get active products
        long activeProducts = productRepository.findAllActiveProducts().size();
        
        // Get low stock products
        long lowStockProducts = productRepository.findLowStockProducts().size();
        
        // Get out of stock products
        long outOfStockProducts = productRepository.findAll().stream()
                .filter(product -> product.getStock() == 0)
                .count();
        
        // Get products by category
        Map<String, Long> productsByCategory = productRepository.findAll().stream()
                .filter(product -> product.getCategory() != null)
                .collect(Collectors.groupingBy(
                        product -> product.getCategory().getCategoryName(),
                        Collectors.counting()));
        
        // Build the response
        Map<String, Object> result = new HashMap<>();
        result.put("totalProducts", totalProducts);
        result.put("activeProducts", activeProducts);
        result.put("lowStockProducts", lowStockProducts);
        result.put("outOfStockProducts", outOfStockProducts);
        result.put("productsByCategory", productsByCategory);
        
        return result;
    }

    @Override
    public Map<String, Object> getUserStatistics() {
        // Get total users
        long totalUsers = userRepository.count();
        
        // Get active users
        long activeUsers = userRepository.findAllActiveUsers().size();
        
        // Get admins
        long admins = userRepository.findAllAdmins().size();
        
        // Get customers
        long customers = userRepository.findAllCustomers().size();
        
        // Get new users by month
        Map<String, Long> newUsersByMonth = userRepository.findAll().stream()
                .filter(user -> user.getCreateAt() != null)
                .collect(Collectors.groupingBy(
                        user -> user.getCreateAt().getYear() + "-" + user.getCreateAt().getMonthValue(),
                        Collectors.counting()));
        
        // Build the response
        Map<String, Object> result = new HashMap<>();
        result.put("totalUsers", totalUsers);
        result.put("activeUsers", activeUsers);
        result.put("admins", admins);
        result.put("customers", customers);
        result.put("newUsersByMonth", newUsersByMonth);
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getTopSellingProducts(Integer limit) {
        List<Object[]> topSellingProductsData = orderDetailRepository.findTopSellingProducts(null);
        List<Map<String, Object>> topSellingProducts = new ArrayList<>();
        
        int count = 0;
        for (Object[] row : topSellingProductsData) {
            if (limit != null && count >= limit) {
                break;
            }
            
            Integer productId = (Integer) row[0];
            Long soldQuantity = (Long) row[1];
            
            Optional<Product> optionalProduct = productRepository.findById(productId);
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                Map<String, Object> productMap = new HashMap<>();
                productMap.put("id", product.getId());
                productMap.put("name", product.getName());
                productMap.put("price", product.getPrice());
                productMap.put("soldQuantity", soldQuantity);
                topSellingProducts.add(productMap);
                count++;
            }
        }
        
        return topSellingProducts;
    }

    @Override
    public List<Map<String, Object>> getTopCustomers(Integer limit) {
        return userRepository.findAll().stream()
                .filter(user -> !user.getRole()) // Only customers
                .sorted(Comparator.comparing(user -> orderRepository.countOrdersByUser(user), Comparator.reverseOrder()))
                .limit(limit != null ? limit : 10)
                .map(user -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", user.getId());
                    userMap.put("userName", user.getUserName());
                    userMap.put("email", user.getEmail());
                    userMap.put("orderCount", orderRepository.countOrdersByUser(user));
                    return userMap;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getSalesByCategory() {
        Map<String, Float> salesByCategory = new HashMap<>();
        
        // Get all orders with status 4 (Completed)
        List<Order> completedOrders = orderRepository.findByOrderStatus(4);
        
        // Calculate sales by category
        for (Order order : completedOrders) {
            orderDetailRepository.findByOrder(order).forEach(orderDetail -> {
                Product product = orderDetail.getProduct();
                if (product != null && product.getCategory() != null) {
                    String categoryName = product.getCategory().getCategoryName();
                    Float price = product.getPrice().floatValue() * orderDetail.getQuantity();
                    
                    salesByCategory.put(
                            categoryName,
                            salesByCategory.getOrDefault(categoryName, 0f) + price
                    );
                }
            });
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("salesByCategory", salesByCategory);
        
        return result;
    }

    @Override
    public Map<String, Object> getSalesByPaymentMethod() {
        Map<String, Float> salesByPaymentMethod = new HashMap<>();
        
        // Get all orders with status 4 (Completed)
        List<Order> completedOrders = orderRepository.findByOrderStatus(4);
        
        // Calculate sales by payment method
        for (Order order : completedOrders) {
            if (order.getPaymentMethod() != null) {
                String methodName = order.getPaymentMethod().getMethodName();
                Float totalPrice = order.getTotalPrice();
                
                salesByPaymentMethod.put(
                        methodName,
                        salesByPaymentMethod.getOrDefault(methodName, 0f) + totalPrice
                );
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("salesByPaymentMethod", salesByPaymentMethod);
        
        return result;
    }

    @Override
    public Map<String, Object> getSalesByShippingMethod() {
        Map<String, Float> salesByShippingMethod = new HashMap<>();
        
        // Get all orders with status 4 (Completed)
        List<Order> completedOrders = orderRepository.findByOrderStatus(4);
        
        // Calculate sales by shipping method
        for (Order order : completedOrders) {
            if (order.getShippingMethod() != null) {
                String methodName = order.getShippingMethod().getMethodName();
                Float totalPrice = order.getTotalPrice();
                
                salesByShippingMethod.put(
                        methodName,
                        salesByShippingMethod.getOrDefault(methodName, 0f) + totalPrice
                );
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("salesByShippingMethod", salesByShippingMethod);
        
        return result;
    }

    @Override
    public Map<String, Object> getRevenueByMonth(Integer year) {
        Map<Integer, Float> revenueByMonth = new HashMap<>();
        
        // Initialize all months with 0
        for (int month = 1; month <= 12; month++) {
            revenueByMonth.put(month, 0f);
        }
        
        // Get all orders with status 4 (Completed)
        List<Order> completedOrders = orderRepository.findByOrderStatus(4);
        
        // Calculate revenue by month
        for (Order order : completedOrders) {
            LocalDateTime createAt = order.getCreateAt();
            if (createAt != null && createAt.getYear() == year) {
                int month = createAt.getMonthValue();
                Float totalPrice = order.getTotalPrice();
                
                revenueByMonth.put(
                        month,
                        revenueByMonth.getOrDefault(month, 0f) + totalPrice
                );
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("year", year);
        result.put("revenueByMonth", revenueByMonth);
        
        return result;
    }

    @Override
    public Map<String, Object> getRevenueByDay(LocalDateTime startDate, LocalDateTime endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startDateStr = startDate.format(formatter);
        String endDateStr = endDate.format(formatter);
        
        List<Object[]> salesByDay = orderRepository.getSalesByDay(startDateStr, endDateStr);
        Map<String, Float> salesByDayMap = new LinkedHashMap<>();
        
        for (Object[] row : salesByDay) {
            String date = row[0].toString();
            Float sales = ((Number) row[1]).floatValue();
            salesByDayMap.put(date, sales);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("startDate", startDateStr);
        result.put("endDate", endDateStr);
        result.put("revenueByDay", salesByDayMap);
        
        return result;
    }
    @Override
    public Map<String, Object> getRevenueByTimeInterval(LocalDateTime startDate, LocalDateTime endDate, String interval) {
        // Validate interval
        if (!Arrays.asList("day", "week", "month", "year").contains(interval)) {
            throw new IllegalArgumentException("Interval must be one of: day, week, month, year");
        }

        // Format dates for SQL query
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startDateStr = startDate.format(formatter);
        String endDateStr = endDate.format(formatter);

        // Get revenue data from repository based on interval
        List<Object[]> revenueData;

        switch (interval) {
            case "day":
                revenueData = statisticsRepository.getRevenueByDay(startDateStr, endDateStr);
                break;
            case "week":
                revenueData = statisticsRepository.getRevenueByWeek(startDateStr, endDateStr);
                break;
            case "month":
                revenueData = statisticsRepository.getRevenueByMonth(startDateStr, endDateStr);
                break;
            case "year":
                revenueData = statisticsRepository.getRevenueByYear(startDateStr, endDateStr);
                break;
            default:
                revenueData = statisticsRepository.getRevenueByDay(startDateStr, endDateStr);
        }

        // Process data
        Map<String, Float> revenueByPeriod = new LinkedHashMap<>();

        for (Object[] row : revenueData) {
            String period = row[0].toString();
            Float revenue = ((Number) row[1]).floatValue();
            revenueByPeriod.put(period, revenue);
        }

        Float totalRevenue = revenueByPeriod.values().stream().reduce(0f, Float::sum);
        Float avgRevenue = revenueByPeriod.isEmpty() ? 0f : totalRevenue / revenueByPeriod.size();

        // Build response
        Map<String, Object> result = new HashMap<>();
        result.put("startDate", startDateStr);
        result.put("endDate", endDateStr);
        result.put("interval", interval);
        result.put("revenueData", revenueByPeriod);
        result.put("totalRevenue", totalRevenue);
        result.put("averageRevenue", avgRevenue);

        return result;
    }

    @Override
    public Map<String, Object> getRevenueByCategoryPieChart(LocalDateTime startDate, LocalDateTime endDate) {
        // Format dates for SQL query if provided
        String startDateStr = null;
        String endDateStr = null;

        if (startDate != null && endDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            startDateStr = startDate.format(formatter);
            endDateStr = endDate.format(formatter);
        }

        // Get revenue by category data
        List<Object[]> results = statisticsRepository.getRevenueByCategoryPieChart(startDateStr, endDateStr);

        // Process data
        Map<String, Float> revenueByCategoryMap = new HashMap<>();
        for (Object[] row : results) {
            String categoryName = (String) row[0];
            Float revenue = ((Number) row[1]).floatValue();
            revenueByCategoryMap.put(categoryName, revenue);
        }

        // Calculate total revenue
        Float totalRevenue = revenueByCategoryMap.values().stream().reduce(0f, Float::sum);

        // Calculate percentage for each category
        List<Map<String, Object>> categoryData = new ArrayList<>();
        for (Map.Entry<String, Float> entry : revenueByCategoryMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("category", entry.getKey());
            item.put("revenue", entry.getValue());
            item.put("percentage", totalRevenue > 0 ? (entry.getValue() / totalRevenue) * 100 : 0);
            categoryData.add(item);
        }

        // Build response
        Map<String, Object> result = new HashMap<>();
        if (startDate != null && endDate != null) {
            result.put("startDate", startDateStr);
            result.put("endDate", endDateStr);
        }
        result.put("totalRevenue", totalRevenue);
        result.put("categoryData", categoryData);

        return result;
    }
}
