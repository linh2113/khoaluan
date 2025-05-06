package com.example.electronics_store.controller;

import com.example.electronics_store.dto.ApiResponse;
import com.example.electronics_store.dto.RatingDTO;
import com.example.electronics_store.dto.ReplyDTO;
import com.example.electronics_store.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ratings")
@CrossOrigin(origins = "*")
public class RatingController {

    private final RatingService ratingService;

    @Autowired
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createRating(
            @RequestParam Integer userId,
            @RequestParam Integer productId,
            @Valid @RequestBody RatingDTO ratingDTO) {
        try {
            RatingDTO rating = ratingService.createRating(userId, productId, ratingDTO);
            return ResponseEntity.ok(ApiResponse.success("Rating created successfully", rating));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<ApiResponse<?>> updateRating(
//            @PathVariable Integer id,
//            @Valid @RequestBody RatingDTO ratingDTO) {
//        try {
//            RatingDTO rating = ratingService.updateRating(id, ratingDTO);
//            return ResponseEntity.ok(ApiResponse.success("Rating updated successfully", rating));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(ApiResponse.error(e.getMessage()));
//        }
//    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getRatingById(@PathVariable Integer id) {
        try {
            RatingDTO rating = ratingService.getRatingById(id);
            return ResponseEntity.ok(ApiResponse.success(rating));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<?>> getRatingsByProductId(@PathVariable Integer productId) {
        try {
            List<RatingDTO> ratings = ratingService.getRatingsByProductId(productId);
            return ResponseEntity.ok(ApiResponse.success(ratings));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<?>> getRatingsByUserId(@PathVariable Integer userId) {
        try {
            List<RatingDTO> ratings = ratingService.getRatingsByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success(ratings));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/reply")
    public ResponseEntity<ApiResponse<?>> replyToRating(
            @RequestParam Integer userId,
            @RequestParam Integer parentRatingId,
            @Valid @RequestBody ReplyDTO replyDTO) {
        try {
            RatingDTO reply = ratingService.replyToRating(userId, parentRatingId, replyDTO.getComment());
            return ResponseEntity.ok(ApiResponse.success("Reply added successfully", reply));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/replies/{parentId}")
    public ResponseEntity<ApiResponse<?>> getRepliesByParentId(@PathVariable Integer parentId) {
        try {
            List<RatingDTO> replies = ratingService.getRepliesByParentId(parentId);
            return ResponseEntity.ok(ApiResponse.success(replies));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/product/{productId}/average")
    public ResponseEntity<ApiResponse<?>> getAverageRatingForProduct(@PathVariable Integer productId) {
        try {
            Double averageRating = ratingService.getAverageRatingForProduct(productId);
            return ResponseEntity.ok(ApiResponse.success(averageRating));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/product/{productId}/count")
    public ResponseEntity<ApiResponse<?>> countRatingsByProduct(@PathVariable Integer productId) {
        try {
            Long count = ratingService.countRatingsByProduct(productId);
            return ResponseEntity.ok(ApiResponse.success(count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/product/{productId}/distribution")
    public ResponseEntity<ApiResponse<?>> getRatingDistributionForProduct(@PathVariable Integer productId) {
        try {
            Map<Integer, Long> distribution = ratingService.getRatingDistributionForProduct(productId);
            return ResponseEntity.ok(ApiResponse.success(distribution));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }


}
