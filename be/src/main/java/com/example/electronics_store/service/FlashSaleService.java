package com.example.electronics_store.service;

import com.example.electronics_store.dto.FlashSaleDTO;
import com.example.electronics_store.dto.FlashSaleItemDTO;
import com.example.electronics_store.dto.ProductDTO;
import com.example.electronics_store.model.FlashSale;

import java.util.List;
import java.util.Optional;

public interface FlashSaleService {
    FlashSaleDTO createFlashSale(FlashSaleDTO flashSaleDTO);
    
    FlashSaleDTO updateFlashSale(Integer id, FlashSaleDTO flashSaleDTO);
    
    FlashSaleDTO getFlashSaleById(Integer id);
    
    List<FlashSaleDTO> getAllFlashSales();
    
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
}
