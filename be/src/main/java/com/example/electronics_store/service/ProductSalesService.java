package com.example.electronics_store.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.electronics_store.dto.ProductDTO;

import java.util.List;

public interface ProductSalesService {

    /**
     * Update sold quantity when order is completed
     * @param productId Product ID
     * @param quantity Quantity sold
     */
    void updateSoldQuantity(Integer productId, Integer quantity);

    /**
     * Recalculate sold quantity for a specific product from order details
     * @param productId Product ID
     */
    void recalculateSoldQuantity(Integer productId);

    /**
     * Batch recalculate sold quantities for all products
     * This should be run periodically to ensure data consistency
     * Also used for initial setup when implementing this feature
     */
    void recalculateAllSoldQuantities();

    /**
     * Get best selling products based on sold quantity
     * @param pageable Pagination parameters
     * @return Page of best selling products
     */
    Page<ProductDTO> getBestSellingProducts(Pageable pageable);


    /**
     * Initialize sold quantities for existing products
     * This is an alias for recalculateAllSoldQuantities() with clearer naming for initial setup
     * @deprecated Use recalculateAllSoldQuantities() instead
     */
    @Deprecated
    default void initializeSoldQuantities() {
        recalculateAllSoldQuantities();
    }
}
