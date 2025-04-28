package com.example.electronics_store.service;

import com.example.electronics_store.dto.ProductDTO;
import com.example.electronics_store.dto.UserProductPredictionDTO;

import java.util.List;

public interface ProductRecommendationService {
    List<ProductDTO> getRecommendedProductsForUser(Integer userId, Integer limit);
    
    List<ProductDTO> getSimilarProducts(Integer productId, Integer limit);
    
    List<ProductDTO> getPopularProducts(Integer limit);
    
    List<ProductDTO> getPersonalizedRecommendations(Integer userId, Integer limit);
    
    void recordUserEvent(Integer userId, Integer productId, String eventType);
    
    List<UserProductPredictionDTO> getPredictionsForUser(Integer userId);
    
    void updatePredictionsForUser(Integer userId);
    
    void updateAllPredictions();
}
