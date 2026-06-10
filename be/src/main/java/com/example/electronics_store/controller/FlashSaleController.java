package com.example.electronics_store.controller;

import com.example.electronics_store.dto.ApiResponse;
import com.example.electronics_store.dto.FlashSaleDTO;
import com.example.electronics_store.dto.ProductDTO;
import com.example.electronics_store.service.FlashSaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/flash-sales")
@CrossOrigin(origins = "*")
public class FlashSaleController {

    @Autowired
    private FlashSaleService flashSaleService;

    /**
     * Lấy flash sales đang hoạt động (Public)
     */
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<?>> getCurrentFlashSales() {
            List<FlashSaleDTO> currentFlashSales = flashSaleService.getCurrentFlashSales();
            return ResponseEntity.ok(ApiResponse.success("Flash sale đang hoạt động", currentFlashSales));
    }

    /**
     * Lấy flash sales sắp tới (Public)
     */
    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<?>> getUpcomingFlashSales() {
            List<FlashSaleDTO> upcomingFlashSales = flashSaleService.getUpcomingFlashSales();
            return ResponseEntity.ok(ApiResponse.success("Flash sale sắp tới", upcomingFlashSales));
    }

    /**
     * Lấy flash sales đã kết thúc (Public)
     */
    @GetMapping("/past")
    public ResponseEntity<ApiResponse<?>> getPastFlashSales() {
            List<FlashSaleDTO> pastFlashSales = flashSaleService.getPastFlashSales();
            return ResponseEntity.ok(ApiResponse.success("Flash sale đã kết thúc", pastFlashSales));
      
    }

    /**
     * Lấy chi tiết flash sale theo ID (Public)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getFlashSaleById(@PathVariable Integer id) {
            FlashSaleDTO flashSale = flashSaleService.getFlashSaleById(id);
            return ResponseEntity.ok(ApiResponse.success("Lấy flash sale thành công", flashSale));
       
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<ApiResponse<?>> getFlashSaleProducts(
        @PathVariable Integer id,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) Integer minPrice,
        @RequestParam(required = false) Integer maxPrice,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductDTO> products = flashSaleService.getFlashSaleProductsWithFilters(
                id, search, minPrice, maxPrice, pageable);
        return ResponseEntity.ok(ApiResponse.success("Lấy sản phẩm trong flash sale thành công", products));
    
    }

    /**
     * Kiểm tra sản phẩm có trong flash sale không (Public)
     */
    @GetMapping("/check-product/{productId}")
    public ResponseEntity<ApiResponse<?>> checkProductInFlashSale(@PathVariable Integer productId) {
            boolean isInFlashSale = flashSaleService.isProductInFlashSale(productId);
            return ResponseEntity.ok(ApiResponse.success("Sản phẩm có trong flash sale không", isInFlashSale));
     
    }

    
}
