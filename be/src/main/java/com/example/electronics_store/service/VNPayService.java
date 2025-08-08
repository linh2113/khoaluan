package com.example.electronics_store.service;

import com.example.electronics_store.dto.PaymentResponseDTO;

import java.util.Map;

public interface VNPayService {
    
    String createPaymentUrl(Integer orderId, Long amount, String orderInfo, String ipAddress);
    
    PaymentResponseDTO processPaymentReturn(Map<String, String> vnpParams);
}