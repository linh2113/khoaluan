package com.example.electronics_store.service.impl;

import com.example.electronics_store.dto.DashboardStatsDTO;
import com.example.electronics_store.model.Order;
import com.example.electronics_store.model.Product;
import com.example.electronics_store.model.User;
import com.example.electronics_store.repository.*;
import com.example.electronics_store.service.OrderService;
import com.example.electronics_store.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final ShippingMethodRepository shippingMethodRepository;
    private final OrderService orderService;

    @Autowired
    public StatisticsServiceImpl(
            OrderRepository orderRepository,
            OrderDetailRepository orderDetailRepository,
            UserRepository userRepository,
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            PaymentMethodRepository paymentMethodRepository,
            ShippingMethodRepository shippingMethodRepository,
            OrderService orderService) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.shippingMethodRepository = shippingMethodRepository;
        this.orderService = orderService;
    }

    @Override
    public Map<String, Object> getDashboardStatistics() {
        // Get counts
        long totalUsers = userRepository.count();
        long totalProducts = productRepository.count();
        long totalOrders = orderRepository.count();
        
        // Get revenue
        Float totalRevenue = orderRepository.calculateTotalSalesInDateRange(
                LocalDateTime.now().minusYears(100), LocalDateTime.now());
        
        // Get today's stats
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        
        long newUsersToday = userRepository.findAll().stream()
                .filter(user -> user.getCreateAt() != null && user.getCreateAt().isAfter(startOfDay))
                .count();
        
        long newOrdersToday = orderRepository.findAll().stream()
                .filter(order -> order.getCreateAt() != null && order.getCreateAt().isAfter(startOfDay))
                .count();
        
        Float revenueToday = orderRepository.calculateTotalSalesInDateRange(startOfDay, LocalDateTime.now());
        
        // Get order status counts
        long pendingOrders = orderRepository.findByOrderStatus(0).size();
        long processingOrders = orderRepository.findByOrderStatus(1).size();
        long shippedOrders = orderRepository.findByOrderStatus(2).size();
        long deliveredOrders = orderRepository.findByOrderStatus(3).size();
        long cancelledOrders = orderRepository.findByOrderStatus(5).size();
        
        // Get low stock products
        long lowStockProducts = productRepository.findLowStockProducts().size();
        
        // Get recent orders
        List<Map<String, Object>> recentOrders = orderRepository.findAll().stream()
                .sorted(Comparator.comparing(Order::getCreateAt).reversed())
                .limit(5)
                .map(order -> {
                    Map<String, Object> orderMap = new HashMap<>();
                    orderMap.put("id", order.getId());
                    orderMap.put("userId", order.getUser().getId());
                    orderMap.put("userName", order.getUser().getUserName());
                    orderMap.put("totalPrice", order.getTotalPrice());
                    orderMap.put("createAt", order.getCreateAt());
                    orderMap.put("orderStatus", order.getOrderStatus());
                    return orderMap;
                })
                .collect(Collectors.toList());
        
        // Get top selling products
        List<Object[]> topSellingProductsData = orderDetailRepository.findTopSellingProducts(null);
        List<Map<String, Object>> topSellingProducts = new ArrayList<>();
        
        for (Object[] row : topSellingProductsData) {
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
            }
        }
        
        // Get top customers
        List<Map<String, Object>> topCustomers = userRepository.findAll().stream()
                .sorted(Comparator.comparing(user -> orderRepository.countOrdersByUser(user), Comparator.reverseOrder()))
                .limit(5)
                .map(user -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", user.getId());
                    userMap.put("userName", user.getUserName());
                    userMap.put("email", user.getEmail());
                    userMap.put("role", user.getRole() ? "Admin" : "Customer"); // Thêm Role
                    userMap.put("orderCount", orderRepository.countOrdersByUser(user));
                    return userMap;
                })
                .collect(Collectors.toList());
        
        // Build the response
        DashboardStatsDTO stats = DashboardStatsDTO.builder()
                .totalUsers(totalUsers)
                .totalProducts(totalProducts)
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue != null ? totalRevenue : 0f)
                .newUsersToday(newUsersToday)
                .newOrdersToday(newOrdersToday)
                .revenueToday(revenueToday != null ? revenueToday : 0f)
                .pendingOrders(pendingOrders)
                .processingOrders(processingOrders)
                .shippedOrders(shippedOrders)
                .deliveredOrders(deliveredOrders)
                .cancelledOrders(cancelledOrders)
                .lowStockProducts(lowStockProducts)
                .recentOrders(recentOrders)
                .topSellingProducts(topSellingProducts)
                .topCustomers(topCustomers)
                .build();
        
        // Convert to map
        Map<String, Object> result = new HashMap<>();
        result.put("totalUsers", stats.getTotalUsers());
        result.put("totalProducts", stats.getTotalProducts());
        result.put("totalOrders", stats.getTotalOrders());
        result.put("totalRevenue", stats.getTotalRevenue());
        result.put("newUsersToday", stats.getNewUsersToday());
        result.put("newOrdersToday", stats.getNewOrdersToday());
        result.put("revenueToday", stats.getRevenueToday());
        result.put("pendingOrders", stats.getPendingOrders());
        result.put("processingOrders", stats.getProcessingOrders());
        result.put("shippedOrders", stats.getShippedOrders());
        result.put("deliveredOrders", stats.getDeliveredOrders());
        result.put("cancelledOrders", stats.getCancelledOrders());
        result.put("lowStockProducts", stats.getLowStockProducts());
        result.put("recentOrders", stats.getRecentOrders());
        result.put("topSellingProducts", stats.getTopSellingProducts());
        result.put("topCustomers", stats.getTopCustomers());
        
        return result;
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
}
