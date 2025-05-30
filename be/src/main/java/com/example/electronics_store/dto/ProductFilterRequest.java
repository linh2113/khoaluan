package com.example.electronics_store.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
@Builder
public class ProductFilterRequest {
    private String keyword;
    private Integer categoryId;
    private String brand;
    private Integer minPrice;
    private Integer maxPrice;
    private Boolean isDiscount;
    private Boolean inStock;
    private FilterType filterType;
    private int page;
    private int size;
    private String sortBy;
    private String sortDir;

    public enum FilterType {
        ALL,                // Tất cả sản phẩm
        TOP_SELLING,       // Sản phẩm bán chạy
        NEW_ARRIVALS,      // Sản phẩm mới
        TOP_RATED,         // Sản phẩm đánh giá cao
        RELATED,           // Sản phẩm liên quan
        DISCOUNTED,  // Sản phẩm giảm giá
        FLASH_SALE
        }
    public Pageable getPageable() {
        Sort sort = Sort.by(sortDir.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        return PageRequest.of(page, size, sort);
    }
}
