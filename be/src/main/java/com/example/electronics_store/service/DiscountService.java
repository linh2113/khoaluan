package com.example.electronics_store.service;

import com.example.electronics_store.dto.DiscountDTO;
import com.example.electronics_store.model.Discount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface DiscountService {
    DiscountDTO createDiscount(DiscountDTO discountDTO);
    Page<DiscountDTO> getDiscountsWithSearch(String search, Pageable pageable);
    DiscountDTO updateDiscount(Integer id, DiscountDTO discountDTO);
    
    DiscountDTO getDiscountById(Integer id);
    
    DiscountDTO getDiscountByCode(String code);
    
    List<DiscountDTO> getAllDiscounts();
    
    List<DiscountDTO> getAllActiveDiscounts();
    
    List<DiscountDTO> getAllExpiredDiscounts();
    
    List<DiscountDTO> getAllUpcomingDiscounts();



    boolean isDiscountValid(Integer discountId);

    Float applyDiscount(Integer discountId, Float amount);

    void useDiscount(Integer discountId);

    Optional<Discount> getDiscountEntityById(Integer id);
    
    Optional<Discount> getDiscountEntityByCode(String code);
}
