package com.example.electronics_store.service.impl;

import com.example.electronics_store.dto.RatingDTO;
import com.example.electronics_store.model.Product;
import com.example.electronics_store.model.Rating;
import com.example.electronics_store.model.User;
import com.example.electronics_store.repository.ProductRepository;
import com.example.electronics_store.repository.RatingRepository;
import com.example.electronics_store.repository.UserRepository;
import com.example.electronics_store.service.RatingService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
        Optional<Rating> existingRating = ratingRepository.findByProductAndUserAndParentIsNull(product, user);
        if (existingRating.isPresent()) {
            throw new RuntimeException("You have already rated this product");
        }

        Rating rating = new Rating();
        rating.setUser(user);
        rating.setProduct(product);
        rating.setRating(ratingDTO.getRating());
        rating.setComment(ratingDTO.getComment());
        rating.setParent(null);
        Rating savedRating = ratingRepository.save(rating);
        return mapRatingToDTO(savedRating);
    }

//    @Override
//    @Transactional
//    public RatingDTO updateRating(Integer id, RatingDTO ratingDTO) {
//        Rating rating = ratingRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Rating not found"));
//
//        rating.setRating(ratingDTO.getRating());
//        rating.setComment(ratingDTO.getComment());
//
//        Rating updatedRating = ratingRepository.save(rating);
//        return mapRatingToDTO(updatedRating);
//    }

    @Override
    @Transactional (readOnly = true)
    public RatingDTO getRatingById(Integer id) {
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rating not found"));
        return mapRatingToDTO(rating);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RatingDTO> getRatingsByProductId(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return ratingRepository.findByProductAndParentIsNull(product).stream()
                .map(this::mapRatingToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional (readOnly = true)
    public List<RatingDTO> getRatingsByUserId(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ratingRepository.findByUser(user).stream()
                .map(this::mapRatingToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RatingDTO replyToRating(Integer userId, Integer parentRatingId, String comment) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Rating parentRating = ratingRepository.findById(parentRatingId)
                .orElseThrow(() -> new RuntimeException("Parent rating not found"));

        Rating reply = new Rating();
        reply.setUser(user);
        reply.setProduct(parentRating.getProduct());
        reply.setRating(null); // No rating for replies
        reply.setComment(comment.trim());
        reply.setParent(parentRating);

        Rating savedReply = ratingRepository.save(reply);
        return mapRatingToDTO(savedReply);
    }

    @Override
    @Transactional (readOnly = true)
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
    @Transactional(readOnly = true)
    public Page<RatingDTO> getRatingsWithSearch(String search, Pageable pageable) {
        Specification<Rating> spec = Specification.where(null);
        if (search != null && !search.trim().isEmpty()) {
            String searchTerm = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                Integer ratingId = null;
                Integer productId = null;
                Integer userId = null;
                Integer ratingValue = null;

                try {
                    //Prioritize
                    ratingId = Integer.parseInt(search);
                    productId = ratingId;
                    userId = ratingId; //second
                    // Check if it's a valid rating value (1-5)
                    if (ratingId >= 1 && ratingId <= 5) { //first
                        ratingValue = ratingId;
                    }
                } catch (NumberFormatException ignored) {
                }
                if (ratingId != null) predicates.add(cb.equal(root.get("id"), ratingId));

                if (productId != null) predicates.add(cb.equal(root.get("product").get("id"), productId));

                if (userId != null) predicates.add(cb.equal(root.get("user").get("id"), userId));

                if (ratingValue != null) predicates.add(cb.equal(root.get("rating"), ratingValue));

                // Add text search predicates
                predicates.add(cb.like(cb.lower(root.get("comment")), searchTerm));
                // Search in related product fields
                predicates.add(cb.like(cb.lower(root.get("product").get("name")), searchTerm));
                // Search in related user fields
                predicates.add(cb.like(cb.lower(root.get("user").get("userName")), searchTerm));
                predicates.add(cb.like(cb.lower(root.get("user").get("surName")), searchTerm));
                predicates.add(cb.like(cb.lower(root.get("user").get("lastName")), searchTerm));
                if (search.matches("\\d{4}-\\d{2}(-\\d{2})?")) {
                    predicates.add(cb.like(
                            cb.function("DATE_FORMAT", String.class, root.get("createAt"), cb.literal("%Y-%m-%d")),
                            search + "%"
                    ));
                }
                return cb.or(predicates.toArray(new Predicate[0]));
            });
        }
        spec = spec.and((root, query, cb) -> cb.isNull(root.get("parent")));
        Page<Rating> ratingPage = ratingRepository.findAll(spec, pageable);

        return ratingPage.map(this::mapRatingToDTO);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<RatingDTO> getRatingsByStarWithPagination(Integer starRating, Pageable pageable) {
        Page<Rating> ratingPage = ratingRepository.findByRatingAndParentIsNull(starRating, pageable);
        return ratingPage.map(this::mapRatingToDTO);
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
