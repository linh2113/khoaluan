package com.example.electronics_store.service;

import com.example.electronics_store.dto.FlashSaleDTO;
import com.example.electronics_store.dto.FlashSaleItemDTO;
import com.example.electronics_store.dto.ProductDTO;
import com.example.electronics_store.model.FlashSale;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FlashSaleService {
    FlashSaleDTO createFlashSale(FlashSaleDTO flashSaleDTO);

    FlashSaleDTO updateFlashSale(Integer id, FlashSaleDTO flashSaleDTO);

    FlashSaleDTO getFlashSaleById(Integer id);

    List<FlashSaleDTO> getAllFlashSales();

    Page<FlashSaleDTO> getFlashSalesWithSearch(String search, Pageable pageable);

    List<FlashSaleDTO> getCurrentFlashSales();

    List<FlashSaleDTO> getUpcomingFlashSales();

    List<FlashSaleDTO> getPastFlashSales();

    void deleteFlashSale(Integer id);

    FlashSaleItemDTO addProductToFlashSale(Integer flashSaleId, FlashSaleItemDTO flashSaleItemDTO);

    FlashSaleItemDTO updateFlashSaleItem(Integer flashSaleItemId, FlashSaleItemDTO flashSaleItemDTO);

    void removeProductFromFlashSale(Integer flashSaleItemId);

    List<ProductDTO> getFlashSaleProducts(Integer flashSaleId);

    boolean isProductInFlashSale(Integer productId);

    Optional<FlashSale> getFlashSaleEntityById(Integer id);

    // Lấy flash sale items theo flash sale id
    List<FlashSaleItemDTO> getFlashSaleItems(Integer flashSaleId);

    // Tìm kiếm flash sale theo tên
    List<FlashSaleDTO> searchFlashSalesByName(String name);

    // Lấy thống kê flash sale
    Long countActiveFlashSales();
    Page<ProductDTO> getFlashSaleProductsWithFilters( Integer flashSaleId,String search,Integer minPrice,Integer maxPrice,Pageable pageable);
    // Kiểm tra xem có thể tạo flash sale trong khoảng thời gian không
    boolean canCreateFlashSaleInTimeRange(LocalDateTime startTime, LocalDateTime endTime);
}
