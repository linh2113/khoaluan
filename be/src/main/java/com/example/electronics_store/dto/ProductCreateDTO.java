package com.example.electronics_store.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateDTO {
    @NotNull(message = "Category is required")
    private Integer categoryId;
    
    private Integer discountId;

    private Integer brandId;

    @NotBlank(message = "Product name is required")
    private String name;
    
    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    private Integer price;
    
    private String description;
    
    private String warranty;
    
    private Float weight;
    
    private String dimensions;
    
    @NotNull(message = "Status is required")
    private Boolean status;
    
    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock must be greater than or equal to 0")
    private Integer stock;
    
    // Product detail fields
    private String processor;
    private String ram;
    private String storage;
    private String display;
    private String graphics;
    private String battery;
    private String camera;
    private String operatingSystem;
    private String connectivity;
    private String otherFeatures;
}
