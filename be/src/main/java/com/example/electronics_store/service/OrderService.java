package com.example.electronics_store.service;

import com.example.electronics_store.dto.OrderCreateDTO;
import com.example.electronics_store.dto.OrderDTO;
import com.example.electronics_store.model.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrderService {
    OrderDTO createOrder(Integer userId, OrderCreateDTO orderCreateDTO);
    
    OrderDTO getOrderById(Integer id);
    
    List<OrderDTO> getOrdersByUserId(Integer userId);
    
    List<OrderDTO> getAllOrders();
    
    List<OrderDTO> getOrdersByStatus(Integer status);
    
    List<OrderDTO> getOrdersByPaymentStatus(String paymentStatus);
    
    List<OrderDTO> getOrdersInDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    OrderDTO updateOrderStatus(Integer id, Integer status);
    
    OrderDTO updatePaymentStatus(Integer id, String paymentStatus);
    
    OrderDTO updateTrackingNumber(Integer id, String trackingNumber);
    
    void deleteOrder(Integer id);
    
    Float calculateTotalSalesInDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    Long countOrdersByUser(Integer userId);
    
    Map<String, Object> getOrderStatistics(LocalDateTime startDate, LocalDateTime endDate);
    
    Optional<Order> getOrderEntityById(Integer id);
}
