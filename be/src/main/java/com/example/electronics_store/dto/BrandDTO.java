package com.example.electronics_store.dto;

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
public class BrandDTO {
    private Integer id;

    @NotBlank(message = "Brand name is required")
    private String brandName;

    private String description;

    private String logo;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @NotNull(message = "Status is required")
    private Integer status;

    private Long productCount;
}