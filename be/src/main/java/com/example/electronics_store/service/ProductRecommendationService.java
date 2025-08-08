package com.example.electronics_store.service;

import com.example.electronics_store.dto.ProductDTO;
import com.example.electronics_store.dto.UserProductPredictionDTO;

import java.util.List;

public interface ProductRecommendationService {
    List<ProductDTO> getRecommendedProductsForUser(Integer userId, Integer limit);
}
