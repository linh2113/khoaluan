package com.example.electronics_store.service;

import com.example.electronics_store.dto.ShippingMethodDTO;
import com.example.electronics_store.model.ShippingMethod;

import java.util.List;
import java.util.Optional;

public interface ShippingMethodService {
    ShippingMethodDTO createShippingMethod(ShippingMethodDTO shippingMethodDTO);
    
    ShippingMethodDTO updateShippingMethod(Integer id, ShippingMethodDTO shippingMethodDTO);
    
    ShippingMethodDTO getShippingMethodById(Integer id);
    
    List<ShippingMethodDTO> getAllShippingMethods();
    
    List<ShippingMethodDTO> getAllActiveShippingMethods();
    
    void deleteShippingMethod(Integer id);
    
    void toggleShippingMethodStatus(Integer id);
    
    Optional<ShippingMethod> getShippingMethodEntityById(Integer id);
}
