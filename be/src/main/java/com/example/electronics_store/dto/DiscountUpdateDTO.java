package com.example.electronics_store.dto;

import com.example.electronics_store.model.Discount;
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
    // Các trường có thể cập nhật
    private Double value;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;
    private Integer priority;
    
    // Trường cho product discount
    private Integer discountedPrice;
    

}