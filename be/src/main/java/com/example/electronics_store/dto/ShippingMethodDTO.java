package com.example.electronics_store.dto;

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
public class ShippingMethodDTO {
    private Integer id;
    
    @NotBlank(message = "Method name is required")
    private String methodName;
    
    private String description;
    
    @NotNull(message = "Base cost is required")
    @Min(value = 0, message = "Base cost must be greater than or equal to 0")
    private Float baseCost;
    
    private String estimatedDays;
    
    private Boolean isActive;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
