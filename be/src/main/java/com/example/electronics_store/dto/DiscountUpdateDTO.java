package com.example.electronics_store.dto;

import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

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
    @Transient
    private transient MultipartFile bannerFile;

}