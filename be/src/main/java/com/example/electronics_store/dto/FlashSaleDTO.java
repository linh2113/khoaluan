package com.example.electronics_store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashSaleDTO {
    private Integer id;

    private String name;

    private String description;

    private LocalDateTime startTime;
    
    private LocalDateTime endTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<FlashSaleItemDTO> items;

    private Boolean isActive;

    private Boolean isUpcoming;

    private Boolean isPast;
}

