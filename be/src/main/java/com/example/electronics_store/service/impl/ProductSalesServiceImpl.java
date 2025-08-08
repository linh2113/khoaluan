package com.example.electronics_store.service.impl;

import com.example.electronics_store.dto.ProductDTO;
import com.example.electronics_store.model.Product;
import com.example.electronics_store.repository.ProductRepository;
import com.example.electronics_store.service.ProductSalesService;
import com.example.electronics_store.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductSalesServiceImpl implements ProductSalesService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Override
    @Transactional
    public void updateSoldQuantity(Integer productId, Integer quantity) {
        try {
            productRepository.updateSoldQuantity(productId, quantity);
            log.info("Updated sold quantity for product {}: +{}", productId, quantity);
        } catch (Exception e) {
            log.error("Failed to update sold quantity for product {}: {}", productId, e.getMessage());
            throw new RuntimeException("Failed to update sold quantity", e);
        }
    }

    @Override
    @Transactional
    public void recalculateSoldQuantity(Integer productId) {
        try {
            productRepository.recalculateSoldQuantity(productId);
            log.info("Recalculated sold quantity for product {}", productId);
        } catch (Exception e) {
            log.error("Failed to recalculate sold quantity for product {}: {}", productId, e.getMessage());
            throw new RuntimeException("Failed to recalculate sold quantity", e);
        }
    }

    @Override
    @Transactional
    public void recalculateAllSoldQuantities() {
        try {
            productRepository.recalculateAllSoldQuantities();
            log.info("Recalculated sold quantities for all products");
        } catch (Exception e) {
            log.error("Failed to recalculate all sold quantities: {}", e.getMessage());
            throw new RuntimeException("Failed to recalculate all sold quantities", e);
        }
    }

    @Override
    public Page<ProductDTO> getBestSellingProducts(Pageable pageable) {
        try {
            Page<Product> products = productRepository.findBestSellingProducts(pageable);
            return products.map(this::mapProductToDTO);
        } catch (Exception e) {
            log.error("Failed to get best selling products: {}", e.getMessage());
            throw new RuntimeException("Failed to get best selling products", e);
        }
    }

    private ProductDTO mapProductToDTO(Product product) {
        return productService.getProductById(product.getId());
    }
}
