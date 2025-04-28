package com.example.electronics_store.controller;

import com.example.electronics_store.dto.ApiResponse;
import com.example.electronics_store.dto.ProductDTO;
// import com.example.electronics_store.service.ProductRecommendationService;
import com.example.electronics_store.dto.ProductFilterRequest;
import com.example.electronics_store.service.ProductService;
import com.example.electronics_store.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;
    private final RatingService ratingService;
    // private final ProductRecommendationService recommendationService;

    @Autowired
    public ProductController(ProductService productService,
                            RatingService ratingService
                            /* ProductRecommendationService recommendationService */) {
        this.productService = productService;
        this.ratingService = ratingService;
        // this.recommendationService = recommendationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllProducts(ProductFilterRequest filter) {
        try {
            Page<ProductDTO> products = productService.getAllProducts(filter);
            return ResponseEntity.ok(ApiResponse.success(products));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getProductById(@PathVariable Integer id) {
        try {
            ProductDTO product = productService.getProductById(id);
            return ResponseEntity.ok(ApiResponse.success(product));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }



    /*
    @GetMapping("/recommended")
    public ResponseEntity<ApiResponse<?>> getRecommendedProducts(@RequestParam Integer userId) {
        try {
            List<ProductDTO> products = recommendationService.getRecommendedProductsForUser(userId, 10);
            return ResponseEntity.ok(ApiResponse.success(products));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    */

    /*
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<?>> getPopularProducts() {
        try {
            List<ProductDTO> products = recommendationService.getPopularProducts(10);
            return ResponseEntity.ok(ApiResponse.success(products));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    */

    @GetMapping("/compare")
    public ResponseEntity<ApiResponse<?>> compareProducts(@RequestParam List<Integer> ids) {
        try {
            List<ProductDTO> products = productService.compareProducts(ids);
            return ResponseEntity.ok(ApiResponse.success(products));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }


    /*
    @PostMapping("/{id}/view")
    public ResponseEntity<ApiResponse<?>> recordProductView(
            @PathVariable Integer id,
            @RequestParam Integer userId) {
        try {
            recommendationService.recordUserEvent(userId, id, "view");
            return ResponseEntity.ok(ApiResponse.success("Product view recorded"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    */
}
