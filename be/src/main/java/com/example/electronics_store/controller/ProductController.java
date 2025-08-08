package com.example.electronics_store.controller;

import com.example.electronics_store.dto.ApiResponse;
import com.example.electronics_store.dto.BrandDTO;
import com.example.electronics_store.dto.CategoryDTO;
import com.example.electronics_store.dto.ProductDTO;
// import com.example.electronics_store.service.ProductRecommendationService;
import com.example.electronics_store.dto.ProductFilterRequest;
import com.example.electronics_store.service.BrandService;
import com.example.electronics_store.service.CategoryService;
import com.example.electronics_store.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    // private final ProductRecommendationService recommendationService;

    @Autowired
    public ProductController(ProductService productService, CategoryService categoryService, BrandService brandService
                        ) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.brandService = brandService;
        // this.recommendationService = recommendationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllProducts(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) Boolean isDiscount,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) ProductFilterRequest.FilterType filterType) {

        try {
            ProductFilterRequest filter = ProductFilterRequest.builder()
                    .page(page)
                    .size(size)
                    .sortBy(sortBy)
                    .sortDir(sortDir)
                    .keyword(keyword)
                    .categoryId(categoryId)
                    .brand(brand)
                    .minPrice(minPrice)
                    .maxPrice(maxPrice)
                    .isDiscount(isDiscount)
                    .inStock(inStock)
                    .filterType(filterType)
                    .build();

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

     @GetMapping("/allCategories")
    public ResponseEntity<ApiResponse<?>> getAllCategories(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() :
                    Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<CategoryDTO> categoryPage = categoryService.getCategoriesWithSearch(search, pageable);

            return ResponseEntity.ok(ApiResponse.success(categoryPage));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    @GetMapping("/allBrands")
    public ResponseEntity<ApiResponse<?>> getAllBrands(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() :
                    Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<BrandDTO> brandPage = brandService.getBrandsWithSearch(search, pageable);

            return ResponseEntity.ok(ApiResponse.success(brandPage));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

}
