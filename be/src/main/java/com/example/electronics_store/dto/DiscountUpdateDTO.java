package com.example.electronics_store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountUpdateDTO {
    private String name;
    private Double value;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;
    private String bannerUrl;
    // Trường cho product discount
    private Integer discountedPrice;
    

}