package com.example.electronics_store.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDTO {
    private Integer id;
    private String imageUrl;
    private Boolean isPrimary;
    private Integer displayOrder;
}
