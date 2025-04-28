package com.example.electronics_store.service;

import com.example.electronics_store.dto.PaymentMethodDTO;
import com.example.electronics_store.model.PaymentMethod;

import java.util.List;
import java.util.Optional;

public interface PaymentMethodService {
    PaymentMethodDTO createPaymentMethod(PaymentMethodDTO paymentMethodDTO);
    
    PaymentMethodDTO updatePaymentMethod(Integer id, PaymentMethodDTO paymentMethodDTO);
    
    PaymentMethodDTO getPaymentMethodById(Integer id);
    
    List<PaymentMethodDTO> getAllPaymentMethods();
    
    List<PaymentMethodDTO> getAllActivePaymentMethods();
    
    void deletePaymentMethod(Integer id);
    
    void togglePaymentMethodStatus(Integer id);
    
    Optional<PaymentMethod> getPaymentMethodEntityById(Integer id);
}
