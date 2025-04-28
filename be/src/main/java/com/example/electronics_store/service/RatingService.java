package com.example.electronics_store.service;

import com.example.electronics_store.dto.RatingDTO;
import com.example.electronics_store.model.Rating;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RatingService {
    RatingDTO createRating(Integer userId, Integer productId, RatingDTO ratingDTO);
    
    RatingDTO updateRating(Integer id, RatingDTO ratingDTO);
    
    RatingDTO getRatingById(Integer id);
    
    List<RatingDTO> getRatingsByProductId(Integer productId);
    
    List<RatingDTO> getRatingsByUserId(Integer userId);
    
    RatingDTO replyToRating(Integer userId, Integer parentRatingId, RatingDTO ratingDTO);
    
    List<RatingDTO> getRepliesByParentId(Integer parentId);
    
    Double getAverageRatingForProduct(Integer productId);
    
    Long countRatingsByProduct(Integer productId);
    
    Map<Integer, Long> getRatingDistributionForProduct(Integer productId);
    

    
    Optional<Rating> getRatingEntityById(Integer id);
}
