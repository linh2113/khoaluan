package com.example.electronics_store.scheduler;

import com.example.electronics_store.model.Order;
import com.example.electronics_store.repository.OrderRepository;
import com.example.electronics_store.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderStatusScheduler {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderService orderService;
    
    /**
     * Tự động chuyển đơn hàng từ Delivered sang Completed sau 15 ngày
     * Chạy mỗi ngày lúc 1 giờ sáng
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void autoCompleteDeliveredOrders() {
        // Tìm các đơn hàng (status = 3) trước 15 ngày
        LocalDateTime fifteenDaysAgo = LocalDateTime.now().minusDays(15);
        
        List<Order> ordersToComplete = orderRepository.findOrdersToAutoComplete(fifteenDaysAgo);
        
        for (Order order : ordersToComplete) {
            try {
                orderService.updateOrderStatus(order.getId(), 4);
            } catch (Exception e) {
                // Log lỗi nhưng vẫn tiếp tục xử lý các đơn hàng khác
                System.err.println("Error auto-completing order #" + order.getId() + ": " + e.getMessage());
            }
        }
    }
}