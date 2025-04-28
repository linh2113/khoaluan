package com.example.electronics_store.controller;

import com.example.electronics_store.dto.ApiResponse;
import com.example.electronics_store.dto.OrderCreateDTO;
import com.example.electronics_store.dto.OrderDTO;
import com.example.electronics_store.service.OrderService;
// import com.example.electronics_store.service.ProductRecommendationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;
    // private final ProductRecommendationService recommendationService;

    @Autowired
    public OrderController(OrderService orderService
                          /* ProductRecommendationService recommendationService */) {
        this.orderService = orderService;
        // this.recommendationService = recommendationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createOrder(
            @RequestParam Integer userId,
            @Valid @RequestBody OrderCreateDTO orderCreateDTO) {
        try {
            OrderDTO order = orderService.createOrder(userId, orderCreateDTO);

            // Record transaction events for each product in the order
            /*
            order.getOrderDetails().forEach(detail -> {
                recommendationService.recordUserEvent(userId, detail.getProductId(), "transaction");
            });
            */

            return ResponseEntity.ok(ApiResponse.success("Order created successfully", order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getOrderById(@PathVariable Integer id) {
        try {
            OrderDTO order = orderService.getOrderById(id);
            return ResponseEntity.ok(ApiResponse.success(order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<?>> getOrdersByUserId(@PathVariable Integer userId) {
        try {
            List<OrderDTO> orders = orderService.getOrdersByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success(orders));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<?>> updateOrderStatus(
            @PathVariable Integer id,
            @RequestParam Integer status) {
        try {
            OrderDTO order = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("Order status updated", order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/payment")
    public ResponseEntity<ApiResponse<?>> updatePaymentStatus(
            @PathVariable Integer id,
            @RequestParam String paymentStatus) {
        try {
            OrderDTO order = orderService.updatePaymentStatus(id, paymentStatus);
            return ResponseEntity.ok(ApiResponse.success("Payment status updated", order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/tracking")
    public ResponseEntity<ApiResponse<?>> updateTrackingNumber(
            @PathVariable Integer id,
            @RequestParam String trackingNumber) {
        try {
            OrderDTO order = orderService.updateTrackingNumber(id, trackingNumber);
            return ResponseEntity.ok(ApiResponse.success("Tracking number updated", order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteOrder(@PathVariable Integer id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.ok(ApiResponse.success("Order deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<?>> countOrdersByUser(@RequestParam Integer userId) {
        try {
            Long count = orderService.countOrdersByUser(userId);
            return ResponseEntity.ok(ApiResponse.success(count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
