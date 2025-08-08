package com.example.electronics_store.service.impl;

import com.example.electronics_store.dto.ShippingMethodDTO;
import com.example.electronics_store.model.ShippingMethod;
import com.example.electronics_store.repository.ShippingMethodRepository;
import com.example.electronics_store.service.ShippingMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShippingMethodServiceImpl implements ShippingMethodService {

    private final ShippingMethodRepository shippingMethodRepository;

    @Autowired
    public ShippingMethodServiceImpl(ShippingMethodRepository shippingMethodRepository) {
        this.shippingMethodRepository = shippingMethodRepository;
    }

    @Override
    @Transactional
    public ShippingMethodDTO createShippingMethod(ShippingMethodDTO shippingMethodDTO) {
        // Kiểm tra tên phương thức vận chuyển đã tồn tại chưa
        if (shippingMethodRepository.existsByMethodName(shippingMethodDTO.getMethodName())) {
            throw new RuntimeException("Shipping method name already exists");
        }

        ShippingMethod shippingMethod = new ShippingMethod();
        shippingMethod.setMethodName(shippingMethodDTO.getMethodName());
        shippingMethod.setDescription(shippingMethodDTO.getDescription());
        shippingMethod.setBaseCost(shippingMethodDTO.getBaseCost());
        shippingMethod.setEstimatedDays(shippingMethodDTO.getEstimatedDays());
        shippingMethod.setIsActive(shippingMethodDTO.getIsActive() != null ? shippingMethodDTO.getIsActive() : true);

        ShippingMethod savedShippingMethod = shippingMethodRepository.save(shippingMethod);
        return mapShippingMethodToDTO(savedShippingMethod);
    }

    @Override
    @Transactional
    public ShippingMethodDTO updateShippingMethod(Integer id, ShippingMethodDTO shippingMethodDTO) {
        ShippingMethod shippingMethod = shippingMethodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipping method not found"));

        // Chỉ cập nhật tên nếu có thay đổi và không trùng với tên khác
        if (shippingMethodDTO.getMethodName() != null && 
            !shippingMethodDTO.getMethodName().equals(shippingMethod.getMethodName())) {
            
            // Kiểm tra tên mới đã tồn tại chưa
            if (shippingMethodRepository.existsByMethodName(shippingMethodDTO.getMethodName())) {
                throw new RuntimeException("Shipping method name already exists");
            }
            shippingMethod.setMethodName(shippingMethodDTO.getMethodName());
        }

        // Cập nhật các trường khác nếu có giá trị mới
        if (shippingMethodDTO.getDescription() != null) {
            shippingMethod.setDescription(shippingMethodDTO.getDescription());
        }
        
        if (shippingMethodDTO.getBaseCost() != null) {
            shippingMethod.setBaseCost(shippingMethodDTO.getBaseCost());
        }
        
        if (shippingMethodDTO.getEstimatedDays() != null) {
            shippingMethod.setEstimatedDays(shippingMethodDTO.getEstimatedDays());
        }
        
        // Cập nhật trạng thái active nếu có giá trị
        if (shippingMethodDTO.getIsActive() != null) {
            shippingMethod.setIsActive(shippingMethodDTO.getIsActive());
        }

        ShippingMethod updatedShippingMethod = shippingMethodRepository.save(shippingMethod);
        return mapShippingMethodToDTO(updatedShippingMethod);
    }

    @Override
    public ShippingMethodDTO getShippingMethodById(Integer id) {
        ShippingMethod shippingMethod = shippingMethodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipping method not found"));
        return mapShippingMethodToDTO(shippingMethod);
    }

    @Override
    public List<ShippingMethodDTO> getAllShippingMethods() {
        return shippingMethodRepository.findAll().stream()
                .map(this::mapShippingMethodToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShippingMethodDTO> getAllActiveShippingMethods() {
        return shippingMethodRepository.findByIsActive(true).stream()
                .map(this::mapShippingMethodToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteShippingMethod(Integer id) {
        if (!shippingMethodRepository.existsById(id)) {
            throw new RuntimeException("Shipping method not found");
        }
        shippingMethodRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void toggleShippingMethodStatus(Integer id) {
        ShippingMethod shippingMethod = shippingMethodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipping method not found"));
        
        shippingMethod.setIsActive(!shippingMethod.getIsActive());
        shippingMethodRepository.save(shippingMethod);
    }

    @Override
    public Optional<ShippingMethod> getShippingMethodEntityById(Integer id) {
        return shippingMethodRepository.findById(id);
    }

    // Helper method to map ShippingMethod entity to DTO
    private ShippingMethodDTO mapShippingMethodToDTO(ShippingMethod shippingMethod) {
        return ShippingMethodDTO.builder()
                .id(shippingMethod.getId())
                .methodName(shippingMethod.getMethodName())
                .description(shippingMethod.getDescription())
                .baseCost(shippingMethod.getBaseCost())
                .estimatedDays(shippingMethod.getEstimatedDays())
                .isActive(shippingMethod.getIsActive())
                .createdAt(shippingMethod.getCreatedAt())
                .updatedAt(shippingMethod.getUpdatedAt())
                .build();
    }
}
