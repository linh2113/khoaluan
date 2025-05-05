package com.example.electronics_store.dto;

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
public class CartDTO {
    private Integer id;
    private Integer userId;
    private LocalDateTime createAt;
    private List<CartItemDTO> items;
    private Float totalPrice;
    private Integer totalItems;
    private Float selectedTotalPrice;
    private Integer selectedTotalItems;
}
