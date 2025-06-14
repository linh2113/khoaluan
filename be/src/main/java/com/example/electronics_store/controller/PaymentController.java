package com.example.electronics_store.controller;

import com.example.electronics_store.dto.ApiResponse;
import com.example.electronics_store.dto.PaymentResponseDTO;
import com.example.electronics_store.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payment")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private VNPayService vnPayService;
    
    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @PostMapping("/create-payment")
    public ResponseEntity<ApiResponse<?>> createPayment(
            @RequestParam Integer orderId,
            @RequestParam Double amount,
            @RequestParam String orderInfo,
            HttpServletRequest request) {
        try {
            // Lấy địa chỉ IP của người dùng
            String ipAddress = request.getRemoteAddr();
            Long amountLong = amount.longValue();
            // Tạo URL thanh toán VNPay
            String paymentUrl = vnPayService.createPaymentUrl(orderId, amountLong, orderInfo, ipAddress);
            
            Map<String, String> response = new HashMap<>();
            response.put("paymentUrl", paymentUrl);
            
            return ResponseEntity.ok(ApiResponse.success("Payment URL created successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<?> vnpayReturn(
            @RequestParam Map<String, String> requestParams,
            HttpServletRequest request) {
        try {
            // Xử lý kết quả trả về từ VNPay
            PaymentResponseDTO response = vnPayService.processPaymentReturn(requestParams);
            
            // Chuyển hướng về trang frontend với kết quả thanh toán
            String redirectUrl = frontendUrl + "/payment/result?success=" + response.isSuccess() 
                    + "&orderId=" + response.getOrderId()
                    + "&message=" + response.getMessage();
            
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", redirectUrl)
                    .build();
        } catch (Exception e) {
            // Chuyển hướng về trang frontend với thông báo lỗi
            String redirectUrl = frontendUrl + "/payment/result?success=false&message=" + e.getMessage();
            
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", redirectUrl)
                    .build();
        }
    }

    @GetMapping("/vnpay-ipn")
    public ResponseEntity<ApiResponse<?>> vnpayIPN(@RequestParam Map<String, String> requestParams) {
        try {
            PaymentResponseDTO response = vnPayService.processPaymentReturn(requestParams);

            if (response.isSuccess()) {
                return ResponseEntity.ok(ApiResponse.success("Payment processed successfully", response));
            } else {
                return ResponseEntity.ok(ApiResponse.error(response.getMessage()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}