package com.example.electronics_store.dto;

import com.example.electronics_store.model.Discount;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountDTO {
    private Integer id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @NotNull(message = "Type is required")
    private Discount.DiscountType type;

    @NotNull(message = "Value is required")
    @Min(value = 0, message = "Value must be greater than or equal to 0")
    private Double value;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDateTime endDate;

    @NotNull(message = "Active status is required")
    private Boolean isActive;

    private Boolean isEffective;

    // Trường cho nhiều sản phẩm
    private List<Integer> productIds;
    private List<String> productNames;
    private Map<Integer, Integer> discountedPrices; // productId -> discountedPrice
    private List<Integer> categoryIds;
    private List<String> categoryNames;
    private String productName;
    private Integer discountedPrice;
    private String categoryName;
    private Integer productId;
    private Integer categoryId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer discountId;
    private Integer assignedCount;
}