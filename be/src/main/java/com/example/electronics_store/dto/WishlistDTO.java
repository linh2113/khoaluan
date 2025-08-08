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
public class WishlistDTO {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private String productName;
    private String productImage;
    private Integer productPrice;
    private Integer discountedPrice;
    private LocalDateTime addedAt;
}
