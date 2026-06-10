package com.example.electronics_store.service;

import com.example.electronics_store.model.Order;

public interface EmailService {
    void sendPaymentSuccessEmail(Order order);
    void sendOrderConfirmationEmail(Order order);
}