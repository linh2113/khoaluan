package com.example.electronics_store.controller;

import com.example.electronics_store.dto.*;
import com.example.electronics_store.service.OrderService;
// import com.example.electronics_store.service.ProductRecommendationService;
import com.example.electronics_store.service.PaymentMethodService;
import com.example.electronics_store.service.ShippingMethodService;
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
    private final PaymentMethodService paymentMethodService;
    private final ShippingMethodService shippingMethodService;
    @Autowired
    public OrderController(OrderService orderService, PaymentMethodService paymentMethodService, ShippingMethodService shippingMethodService
            /* ProductRecommendationService recommendationService */) {
        this.orderService = orderService;
        // this.recommendationService = recommendationService;
        this.paymentMethodService = paymentMethodService;
        this.shippingMethodService = shippingMethodService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createOrder(
            @RequestParam Integer userId,
            @Valid @RequestBody OrderCreateDTO orderCreateDTO) {
            OrderDTO order = orderService.createOrder(userId, orderCreateDTO);

            // Record transaction events for each product in the order
            /*
            order.getOrderDetails().forEach(detail -> {
                recommendationService.recordUserEvent(userId, detail.getProductId(), "transaction");
            });
            */

            return ResponseEntity.ok(ApiResponse.success("Tạo thành công đơn hàng", order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getOrderById(@PathVariable Integer id) {
            OrderDTO order = orderService.getOrderById(id);
            return ResponseEntity.ok(ApiResponse.success(order));
    
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<?>> getOrdersByUserId(@PathVariable Integer userId) {
            List<OrderDTO> orders = orderService.getOrdersByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<?>> updateOrderStatus(
            @PathVariable Integer id,
            @RequestParam Integer status) {
            OrderDTO order = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái đơn hàng thành công", order));
    }

    @PutMapping("/{id}/payment")
    public ResponseEntity<ApiResponse<?>> updatePaymentStatus(
            @PathVariable Integer id,
            @RequestParam String paymentStatus) {
            OrderDTO order = orderService.updatePaymentStatus(id, paymentStatus);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật phương thức thành toán thành công!", order));
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
            orderService.deleteOrder(id);
            return ResponseEntity.ok(ApiResponse.success("Order deleted successfully"));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<?>> countOrdersByUser(@RequestParam Integer userId) {
            Long count = orderService.countOrdersByUser(userId);
            return ResponseEntity.ok(ApiResponse.success(count));
        
    }
    @GetMapping("/payment-methods")
    public ResponseEntity<ApiResponse<?>> getAllActivePaymentMethods() {
            List<PaymentMethodDTO> paymentMethods = paymentMethodService.getAllActivePaymentMethods();
            return ResponseEntity.ok(ApiResponse.success(paymentMethods));
        
    }

    @GetMapping("/shipping-methods")
    public ResponseEntity<ApiResponse<?>> getAllActiveShippingMethods() {
            List<ShippingMethodDTO> shippingMethods = shippingMethodService.getAllActiveShippingMethods();
            return ResponseEntity.ok(ApiResponse.success(shippingMethods));
    }

}
