package com.example.electronics_store.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountDTO {
    private Integer id;
    
    @NotBlank(message = "Code is required")
    private String code;
    
    @NotBlank(message = "Discount name is required")
    private String discountName;
    
    private String description;
    
    @NotNull(message = "Value is required")
    @Min(value = 0, message = "Value must be greater than or equal to 0")
    private Double value;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be greater than or equal to 0")
    private Integer quantity;
    
    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;
    
    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDateTime endDate;
    
    private Boolean isActive;
}
