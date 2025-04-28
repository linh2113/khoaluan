package com.example.electronics_store.service.impl;

import com.example.electronics_store.dto.RatingDTO;
import com.example.electronics_store.model.Product;
import com.example.electronics_store.model.Rating;
import com.example.electronics_store.model.User;
import com.example.electronics_store.repository.ProductRepository;
import com.example.electronics_store.repository.RatingRepository;
import com.example.electronics_store.repository.UserRepository;
import com.example.electronics_store.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Autowired
    public RatingServiceImpl(
            RatingRepository ratingRepository,
            UserRepository userRepository,
            ProductRepository productRepository) {
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public RatingDTO createRating(Integer userId, Integer productId, RatingDTO ratingDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if user has already rated this product
        Optional<Rating> existingRating = ratingRepository.findByProductAndUser(product, user);
        if (existingRating.isPresent()) {
            throw new RuntimeException("You have already rated this product");
        }

        Rating rating = new Rating();
        rating.setUser(user);
        rating.setProduct(product);
        rating.setRating(ratingDTO.getRating());
        rating.setComment(ratingDTO.getComment());

        Rating savedRating = ratingRepository.save(rating);
        return mapRatingToDTO(savedRating);
    }

    @Override
    @Transactional
    public RatingDTO updateRating(Integer id, RatingDTO ratingDTO) {
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rating not found"));

        rating.setRating(ratingDTO.getRating());
        rating.setComment(ratingDTO.getComment());

        Rating updatedRating = ratingRepository.save(rating);
        return mapRatingToDTO(updatedRating);
    }

    @Override
    public RatingDTO getRatingById(Integer id) {
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rating not found"));
        return mapRatingToDTO(rating);
    }

    @Override
    public List<RatingDTO> getRatingsByProductId(Integer productId) {
        if (productId == null) {
            // Return all ratings if productId is null
            return ratingRepository.findAll().stream()
                    .map(this::mapRatingToDTO)
                    .collect(Collectors.toList());
        }
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return ratingRepository.findByProduct(product).stream()
                .map(this::mapRatingToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RatingDTO> getRatingsByUserId(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ratingRepository.findByUser(user).stream()
                .map(this::mapRatingToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RatingDTO replyToRating(Integer userId, Integer parentRatingId, RatingDTO ratingDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Rating parentRating = ratingRepository.findById(parentRatingId)
                .orElseThrow(() -> new RuntimeException("Parent rating not found"));

        Rating reply = new Rating();
        reply.setUser(user);
        reply.setProduct(parentRating.getProduct());
        reply.setRating(null); // No rating for replies
        reply.setComment(ratingDTO.getComment());
        reply.setParent(parentRating);

        Rating savedReply = ratingRepository.save(reply);
        return mapRatingToDTO(savedReply);
    }

    @Override
    public List<RatingDTO> getRepliesByParentId(Integer parentId) {
        Rating parentRating = ratingRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent rating not found"));

        return ratingRepository.findByParent(parentRating).stream()
                .map(this::mapRatingToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Double getAverageRatingForProduct(Integer productId) {
        return ratingRepository.getAverageRatingForProduct(productId);
    }

    @Override
    public Long countRatingsByProduct(Integer productId) {
        return ratingRepository.countRatingsByProduct(productId);
    }

    @Override
    public Map<Integer, Long> getRatingDistributionForProduct(Integer productId) {
        List<Object[]> distribution = ratingRepository.getRatingDistributionForProduct(productId);
        Map<Integer, Long> result = new HashMap<>();

        for (Object[] row : distribution) {
            Integer rating = (Integer) row[0];
            Long count = (Long) row[1];
            result.put(rating, count);
        }

        return result;
    }



    @Override
    public Optional<Rating> getRatingEntityById(Integer id) {
        return ratingRepository.findById(id);
    }

    // Helper method
    private RatingDTO mapRatingToDTO(Rating rating) {
        RatingDTO dto = new RatingDTO();
        dto.setId(rating.getId());
        dto.setProductId(rating.getProduct().getId());
        dto.setProductName(rating.getProduct().getName());
        dto.setUserId(rating.getUser().getId());
        dto.setUserName(rating.getUser().getUserName());
        dto.setUserPicture(rating.getUser().getPicture());
        dto.setRating(rating.getRating());
        dto.setComment(rating.getComment());
        dto.setCreateAt(rating.getCreateAt());
        
        if (rating.getParent() != null) {
            dto.setParentId(rating.getParent().getId());
        }
        
        // Get replies if any
        if (rating.getReplies() != null && !rating.getReplies().isEmpty()) {
            dto.setReplies(rating.getReplies().stream()
                    .map(this::mapRatingToDTO)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
}
