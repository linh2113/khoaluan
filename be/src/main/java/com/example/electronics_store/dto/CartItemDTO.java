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
public class CartItemDTO {
    private Integer id;
    private Integer productId;
    private String productName;
    private String productImage;
    private Integer quantity;
    private Float price;
    private Float totalPrice;
    private LocalDateTime createAt;
    private Integer stock;
}
