package com.example.electronics_store.controller;

import com.example.electronics_store.dto.ApiResponse;
import com.example.electronics_store.dto.ProductDTO;
import com.example.electronics_store.dto.UserProductPredictionDTO;
import com.example.electronics_store.service.ProductRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {


    private ProductRecommendationService recommendationService;

    private final WebClient recClient;

    public RecommendationController(@Value("${recommendation.service.url:http://localhost:8000}") String recommendationServiceUrl) {
        this.recClient = WebClient.builder()
                .baseUrl(recommendationServiceUrl)
                .build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<?>> getRecommendationsForUser(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<ProductDTO> products = recommendationService.getRecommendedProductsForUser(userId, limit);
            return ResponseEntity.ok(ApiResponse.success(products));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/similar/{productId}")
    public ResponseEntity<ApiResponse<?>> getSimilarProducts(
            @PathVariable Integer productId,
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<ProductDTO> products = recommendationService.getSimilarProducts(productId, limit);
            return ResponseEntity.ok(ApiResponse.success(products));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<?>> getPopularProducts(
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<ProductDTO> products = recommendationService.getPopularProducts(limit);
            return ResponseEntity.ok(ApiResponse.success(products));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/personalized/{userId}")
    public ResponseEntity<ApiResponse<?>> getPersonalizedRecommendations(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<ProductDTO> products = recommendationService.getPersonalizedRecommendations(userId, limit);
            return ResponseEntity.ok(ApiResponse.success(products));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/event")
    public ResponseEntity<ApiResponse<?>> recordUserEvent(
            @RequestParam Integer userId,
            @RequestParam Integer productId,
            @RequestParam String eventType) {
        try {
            recommendationService.recordUserEvent(userId, productId, eventType);
            return ResponseEntity.ok(ApiResponse.success("Event recorded successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/predictions/{userId}")
    public ResponseEntity<ApiResponse<?>> getPredictionsForUser(@PathVariable Integer userId) {
        try {
            List<UserProductPredictionDTO> predictions = recommendationService.getPredictionsForUser(userId);
            return ResponseEntity.ok(ApiResponse.success(predictions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/predictions/update/{userId}")
    public ResponseEntity<ApiResponse<?>> updatePredictionsForUser(@PathVariable Integer userId) {
        try {
            recommendationService.updatePredictionsForUser(userId);
            return ResponseEntity.ok(ApiResponse.success("Predictions updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/predictions/update-all")
    public ResponseEntity<ApiResponse<?>> updateAllPredictions() {
        try {
            recommendationService.updateAllPredictions();
            return ResponseEntity.ok(ApiResponse.success("All predictions updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }


}