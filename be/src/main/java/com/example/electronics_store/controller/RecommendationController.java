package com.example.electronics_store.controller;

import com.example.electronics_store.dto.ApiResponse;
import com.example.electronics_store.dto.ProductDTO;
import com.example.electronics_store.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final ProductService  productService;
    private final WebClient    recClient;
    private static final int MAX_K = 30;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<?>> rec(
            @PathVariable String userId,
            @RequestParam(defaultValue = "8") Integer k) {
    try{
        k = Math.max(1, Math.min(k, MAX_K));

        String[] asins = recClient.get()
                .uri("/recommendations/{uid}?k={k}", userId, k)
                .retrieve()
                .bodyToMono(String[].class)
                .block();

        if (asins == null || asins.length == 0)
            return ResponseEntity.ok(ApiResponse.success("No recommendations", List.of()));

        List<ProductDTO> products = productService.getProductsByIdStrings(List.of(asins));
        // Giữ thứ tự theo recommender
        Map<String,Integer> idx = IntStream.range(0, asins.length)
                .boxed()
                .collect(Collectors.toMap(i -> asins[i], i -> i));
        products.sort(Comparator.comparingInt(p -> idx.getOrDefault(p.getProductIdString(), 999)));
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    catch (Exception e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
    }
    }
}
