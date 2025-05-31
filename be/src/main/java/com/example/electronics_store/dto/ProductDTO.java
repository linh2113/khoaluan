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
public class ProductDTO {
    private Integer id;
    private Integer categoryId;
    private String categoryName;
    private Integer brandId;
    private String brandName;
    private String name;
    private String image;
    private Integer price;
    private Integer discountedPrice;
    private LocalDateTime discountStartDate;
    private LocalDateTime discountEndDate;
    private Boolean isDiscountActive;
    private String discountType;
    private Double discountPercentage;
    private Integer discountId;

    private String description;
    private String warranty;
    private Float weight;
    private String dimensions;
    private LocalDateTime createAt;
    private String createBy;
    private Boolean status;
    private LocalDateTime updateAt;
    private String updateBy;
    private Integer stock;
    private Integer soldQuantity;
    private Double averageRating;
    private Long reviewCount;
    private List<ProductImageDTO> productImages;
    private ProductDetailDTO productDetail;
}
