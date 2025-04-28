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
public class NotificationDTO {
    private Integer id;
    private Integer userId;
    private String title;
    private String message;
    private String type;
    private Boolean isRead;
    private Integer relatedId;
    private String relatedType;
    private LocalDateTime createdAt;
}
