package com.example.electronics_store.service;

import com.example.electronics_store.dto.RatingDTO;
import com.example.electronics_store.model.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RatingService {
    RatingDTO createRating(Integer userId, Integer productId, RatingDTO ratingDTO);
    Page<RatingDTO> getRatingsWithSearch(String search, Pageable pageable);
//    RatingDTO updateRating(Integer id, RatingDTO ratingDTO);
    Page<RatingDTO> getRatingsByStarWithPagination(Integer starRating, Pageable pageable);
    RatingDTO getRatingById(Integer id);
    Page<RatingDTO> getRatingsByProductIdWithPagination(Integer productId, Pageable pageable);
    List<RatingDTO> getRatingsByProductId(Integer productId);
    List<RatingDTO> getRatingsByUserId(Integer userId);
    List<String> uploadRatingImages(Integer ratingId, List<MultipartFile> files);
    RatingDTO replyToRating(Integer userId, Integer parentRatingId, String reply);
    
    List<RatingDTO> getRepliesByParentId(Integer parentId);
    
    Double getAverageRatingForProduct(Integer productId);
    
    Long countRatingsByProduct(Integer productId);
    
    Map<Integer, Long> getRatingDistributionForProduct(Integer productId);
    

    
    Optional<Rating> getRatingEntityById(Integer id);
}
