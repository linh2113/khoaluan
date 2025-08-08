package com.example.electronics_store.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class RatingDTO {
    private Integer id;
    private Integer productId;
    private String productName;
    private Integer userId;
    private String userName;
    private String userPicture;
    private String productPicture;
    private List<String> imageUrls;
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer rating;
    
    private String comment;
    private LocalDateTime createAt;
    private Integer parentId;
    private List<RatingDTO> replies;

}
