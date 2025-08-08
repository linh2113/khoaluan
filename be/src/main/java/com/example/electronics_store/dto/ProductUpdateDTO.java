package com.example.electronics_store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateDTO {
    private Integer categoryId;
    private Integer discountId;
    private String name;
    private Integer brandId;
    private Integer price;
    private String description;
    private String warranty;
    private Float weight;
    private String dimensions;
    private Boolean status;
    private Integer stock;
    
    // Technical specifications
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