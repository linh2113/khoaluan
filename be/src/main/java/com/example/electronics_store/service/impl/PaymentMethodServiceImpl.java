package com.example.electronics_store.service.impl;

import com.example.electronics_store.dto.PaymentMethodDTO;
import com.example.electronics_store.model.PaymentMethod;
import com.example.electronics_store.repository.PaymentMethodRepository;
import com.example.electronics_store.service.PaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    @Autowired
    public PaymentMethodServiceImpl(PaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @Override
    @Transactional
    public PaymentMethodDTO createPaymentMethod(PaymentMethodDTO paymentMethodDTO) {
        // Kiểm tra tên phương thức thanh toán đã tồn tại chưa
        if (paymentMethodRepository.existsByMethodName(paymentMethodDTO.getMethodName())) {
            throw new RuntimeException("Payment method name already exists");
        }

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setMethodName(paymentMethodDTO.getMethodName());
        paymentMethod.setDescription(paymentMethodDTO.getDescription());
        paymentMethod.setIsActive(paymentMethodDTO.getIsActive() != null ? paymentMethodDTO.getIsActive() : true);

        PaymentMethod savedPaymentMethod = paymentMethodRepository.save(paymentMethod);
        return mapPaymentMethodToDTO(savedPaymentMethod);
    }

    @Override
    @Transactional
    public PaymentMethodDTO updatePaymentMethod(Integer id, PaymentMethodDTO paymentMethodDTO) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment method not found"));

        // Chỉ cập nhật tên nếu có thay đổi và không trùng với tên khác
        if (paymentMethodDTO.getMethodName() != null && 
            !paymentMethodDTO.getMethodName().equals(paymentMethod.getMethodName())) {
            
            // Kiểm tra tên mới đã tồn tại chưa
            if (paymentMethodRepository.existsByMethodName(paymentMethodDTO.getMethodName())) {
                throw new RuntimeException("Payment method name already exists");
            }
            paymentMethod.setMethodName(paymentMethodDTO.getMethodName());
        }

        // Cập nhật các trường khác nếu có giá trị mới
        if (paymentMethodDTO.getDescription() != null) {
            paymentMethod.setDescription(paymentMethodDTO.getDescription());
        }

        PaymentMethod updatedPaymentMethod = paymentMethodRepository.save(paymentMethod);
        return mapPaymentMethodToDTO(updatedPaymentMethod);
    }

    @Override
    public PaymentMethodDTO getPaymentMethodById(Integer id) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment method not found"));
        return mapPaymentMethodToDTO(paymentMethod);
    }

    @Override
    public List<PaymentMethodDTO> getAllPaymentMethods() {
        return paymentMethodRepository.findAll().stream()
                .map(this::mapPaymentMethodToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentMethodDTO> getAllActivePaymentMethods() {
        return paymentMethodRepository.findByIsActive(true).stream()
                .map(this::mapPaymentMethodToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deletePaymentMethod(Integer id) {
        if (!paymentMethodRepository.existsById(id)) {
            throw new RuntimeException("Payment method not found");
        }
        paymentMethodRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void togglePaymentMethodStatus(Integer id) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment method not found"));
        
        paymentMethod.setIsActive(!paymentMethod.getIsActive());
        paymentMethodRepository.save(paymentMethod);
    }

    @Override
    public Optional<PaymentMethod> getPaymentMethodEntityById(Integer id) {
        return paymentMethodRepository.findById(id);
    }

    // Helper method to map PaymentMethod entity to DTO
    private PaymentMethodDTO mapPaymentMethodToDTO(PaymentMethod paymentMethod) {
        return PaymentMethodDTO.builder()
                .id(paymentMethod.getId())
                .methodName(paymentMethod.getMethodName())
                .description(paymentMethod.getDescription())
                .isActive(paymentMethod.getIsActive())
                .createdAt(paymentMethod.getCreatedAt())
                .updatedAt(paymentMethod.getUpdatedAt())
                .build();
    }
}