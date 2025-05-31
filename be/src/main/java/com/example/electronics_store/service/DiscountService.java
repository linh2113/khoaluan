package com.example.electronics_store.service;

import com.example.electronics_store.dto.DiscountDTO;
import com.example.electronics_store.dto.DiscountUpdateDTO;
import com.example.electronics_store.model.Discount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DiscountService {
    // Các phương thức cho Discount chung
    DiscountDTO createDiscount(DiscountDTO discountDTO);
    DiscountDTO updateDiscount(Integer id, DiscountUpdateDTO discountUpdateDTO);
    void deleteDiscount(Integer id);
    DiscountDTO getDiscountById(Integer id);
    Page<DiscountDTO> getAllDiscounts(String search, Pageable pageable);
    List<DiscountDTO> getAllActiveDiscounts();
    List<DiscountDTO> getAllExpiredDiscounts();
    List<DiscountDTO> getAllUpcomingDiscounts();
    boolean isDiscountValid(Integer discountId);
    Float calculateProductPrice(Integer productId);
    Float calculateProductPriceAtTime(Integer productId, LocalDateTime time);
    Optional<Discount> getDiscountEntityById(Integer id);
    Map<String, Object> getProductDiscountInfo(Integer productId);
    Integer assignDiscountToProducts(Integer discountId, List<Integer> productIds, Map<Integer, Integer> discountedPrices);
    Integer assignDiscountToCategories(Integer discountId, List<Integer> categoryIds);
    // Các phương thức cho Product Discount
    List<DiscountDTO> getProductDiscountsByProductId(Integer productId);
    List<DiscountDTO> getActiveProductDiscountsByProductId(Integer productId);
    List<DiscountDTO> getAllProductDiscounts();

    // Các phương thức cho Category Discount
    List<DiscountDTO> getCategoryDiscountsByCategoryId(Integer categoryId);
    List<DiscountDTO> getActiveCategoryDiscountsByCategoryId(Integer categoryId);
    List<DiscountDTO> getAllCategoryDiscounts();
}