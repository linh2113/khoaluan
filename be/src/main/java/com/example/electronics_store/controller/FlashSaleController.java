package com.example.electronics_store.controller;

import com.example.electronics_store.dto.ApiResponse;
import com.example.electronics_store.dto.FlashSaleDTO;
import com.example.electronics_store.dto.ProductDTO;
import com.example.electronics_store.service.FlashSaleService;
import org.springframework.beans.factory.annotation.Autowired;
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
        try {
            List<FlashSaleDTO> currentFlashSales = flashSaleService.getCurrentFlashSales();
            return ResponseEntity.ok(ApiResponse.success("Current flash sales retrieved successfully", currentFlashSales));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Lấy flash sales sắp tới (Public)
     */
    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<?>> getUpcomingFlashSales() {
        try {
            List<FlashSaleDTO> upcomingFlashSales = flashSaleService.getUpcomingFlashSales();
            return ResponseEntity.ok(ApiResponse.success("Upcoming flash sales retrieved successfully", upcomingFlashSales));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Lấy flash sales đã kết thúc (Public)
     */
    @GetMapping("/past")
    public ResponseEntity<ApiResponse<?>> getPastFlashSales() {
        try {
            List<FlashSaleDTO> pastFlashSales = flashSaleService.getPastFlashSales();
            return ResponseEntity.ok(ApiResponse.success("Past flash sales retrieved successfully", pastFlashSales));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Lấy chi tiết flash sale theo ID (Public)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getFlashSaleById(@PathVariable Integer id) {
        try {
            FlashSaleDTO flashSale = flashSaleService.getFlashSaleById(id);
            return ResponseEntity.ok(ApiResponse.success("Flash sale retrieved successfully", flashSale));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Lấy danh sách sản phẩm trong flash sale (Public)
     */
    @GetMapping("/{id}/products")
    public ResponseEntity<ApiResponse<?>> getFlashSaleProducts(@PathVariable Integer id) {
        try {
            List<ProductDTO> products = flashSaleService.getFlashSaleProducts(id);
            return ResponseEntity.ok(ApiResponse.success("Flash sale products retrieved successfully", products));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Kiểm tra sản phẩm có trong flash sale không (Public)
     */
    @GetMapping("/check-product/{productId}")
    public ResponseEntity<ApiResponse<?>> checkProductInFlashSale(@PathVariable Integer productId) {
        try {
            boolean isInFlashSale = flashSaleService.isProductInFlashSale(productId);
            return ResponseEntity.ok(ApiResponse.success("Product flash sale status checked", isInFlashSale));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    
}
