package com.example.electronics_store.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashSaleItemDTO {
    private Integer id;
    
    private Integer flashSaleId;
    
    @NotNull(message = "Product ID is required")
    private Integer productId;
    
    private String productName;
    
    private String productImage;
    
    private Integer originalPrice;
    
    @NotNull(message = "Flash price is required")
    @Min(value = 0, message = "Flash price must be greater than or equal to 0")
    private Float flashPrice;
    
    private Integer discountPercentage;
    
    private Integer stockLimit;
    
    private Integer soldCount;
    
    private Integer availableStock;
}
