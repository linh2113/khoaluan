package com.example.electronics_store.controller;

import com.example.electronics_store.dto.ApiResponse;
import com.example.electronics_store.dto.FlashSaleDTO;
import com.example.electronics_store.dto.FlashSaleItemDTO;
import com.example.electronics_store.dto.ProductDTO;
import com.example.electronics_store.service.FlashSaleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import java.util.List;

@RestController
@RequestMapping("/api/flash-sales")
@CrossOrigin(origins = "*")
public class FlashSaleController {

    @Autowired
    private FlashSaleService flashSaleService;

    // ==================== ADMIN ENDPOINTS ====================

    /**
     * Tạo flash sale mới (Admin only)
     */
    @PostMapping("/admin")
    public ResponseEntity<ApiResponse<?>> createFlashSale(@Valid @RequestBody FlashSaleDTO flashSaleDTO) {
        try {
            FlashSaleDTO createdFlashSale = flashSaleService.createFlashSale(flashSaleDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Flash sale created successfully", createdFlashSale));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Cập nhật flash sale (Admin only)
     */
    @PutMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<?>> updateFlashSale(@PathVariable Integer id,
                                                         @Valid @RequestBody FlashSaleDTO flashSaleDTO) {
        try {
            FlashSaleDTO updatedFlashSale = flashSaleService.updateFlashSale(id, flashSaleDTO);
            return ResponseEntity.ok(ApiResponse.success("Flash sale updated successfully", updatedFlashSale));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Xóa flash sale (Admin only)
     */
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<?>> deleteFlashSale(@PathVariable Integer id) {
        try {
            flashSaleService.deleteFlashSale(id);
            return ResponseEntity.ok(ApiResponse.success("Flash sale deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Lấy tất cả flash sales (Admin only)
     */
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<?>> getAllFlashSales(
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
        Page<FlashSaleDTO> flashSalePage = flashSaleService.getFlashSalesWithSearch(search, pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Flash sales retrieved successfully", flashSalePage));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(e.getMessage()));
    }
}

    /**
     * Thêm sản phẩm vào flash sale (Admin only)
     */
    @PostMapping("/admin/{flashSaleId}/products")
    public ResponseEntity<ApiResponse<?>> addProductToFlashSale(@PathVariable Integer flashSaleId,
                                                               @Valid @RequestBody FlashSaleItemDTO flashSaleItemDTO) {
        try {
            FlashSaleItemDTO addedItem = flashSaleService.addProductToFlashSale(flashSaleId, flashSaleItemDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Product added to flash sale successfully", addedItem));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Cập nhật sản phẩm trong flash sale (Admin only)
     */
    @PutMapping("/admin/items/{flashSaleItemId}")
    public ResponseEntity<ApiResponse<?>> updateFlashSaleItem(@PathVariable Integer flashSaleItemId,
                                                             @Valid @RequestBody FlashSaleItemDTO flashSaleItemDTO) {
        try {
            FlashSaleItemDTO updatedItem = flashSaleService.updateFlashSaleItem(flashSaleItemId, flashSaleItemDTO);
            return ResponseEntity.ok(ApiResponse.success("Flash sale item updated successfully", updatedItem));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Xóa sản phẩm khỏi flash sale (Admin only)
     */
    @DeleteMapping("/admin/items/{flashSaleItemId}")
    public ResponseEntity<ApiResponse<?>> removeProductFromFlashSale(@PathVariable Integer flashSaleItemId) {
        try {
            flashSaleService.removeProductFromFlashSale(flashSaleItemId);
            return ResponseEntity.ok(ApiResponse.success("Product removed from flash sale successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ==================== USER ENDPOINTS ====================

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

    /**
     * Lấy danh sách flash sale items theo flash sale id (Admin only)
     */
    @GetMapping("/admin/{flashSaleId}/items")
    public ResponseEntity<ApiResponse<?>> getFlashSaleItems(@PathVariable Integer flashSaleId) {
        try {
            List<FlashSaleItemDTO> items = flashSaleService.getFlashSaleItems(flashSaleId);
            return ResponseEntity.ok(ApiResponse.success("Flash sale items retrieved successfully", items));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Tìm kiếm flash sale theo tên (Admin only)
     */
    @GetMapping("/admin/search")
    public ResponseEntity<ApiResponse<?>> searchFlashSales(@RequestParam String name) {
        try {
            List<FlashSaleDTO> flashSales = flashSaleService.searchFlashSalesByName(name);
            return ResponseEntity.ok(ApiResponse.success("Flash sales found", flashSales));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Lấy thống kê số lượng flash sale đang hoạt động (Admin only)
     */
    @GetMapping("/admin/stats/active-count")
    public ResponseEntity<ApiResponse<?>> getActiveFlashSalesCount() {
        try {
            Long count = flashSaleService.countActiveFlashSales();
            return ResponseEntity.ok(ApiResponse.success("Active flash sales count retrieved", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
