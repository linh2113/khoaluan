package com.example.electronics_store.service;

import com.example.electronics_store.dto.DiscountDTO;
import com.example.electronics_store.model.Discount;

import java.util.List;
import java.util.Optional;

public interface DiscountService {
    DiscountDTO createDiscount(DiscountDTO discountDTO);
    
    DiscountDTO updateDiscount(Integer id, DiscountDTO discountDTO);
    
    DiscountDTO getDiscountById(Integer id);
    
    DiscountDTO getDiscountByCode(String code);
    
    List<DiscountDTO> getAllDiscounts();
    
    List<DiscountDTO> getAllActiveDiscounts();
    
    List<DiscountDTO> getAllExpiredDiscounts();
    
    List<DiscountDTO> getAllUpcomingDiscounts();
    

    
    boolean isDiscountValid(String code);
    
    Float applyDiscount(String code, Float amount);
    
    void useDiscount(String code);
    
    Optional<Discount> getDiscountEntityById(Integer id);
    
    Optional<Discount> getDiscountEntityByCode(String code);
}
