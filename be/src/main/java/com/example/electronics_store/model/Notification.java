package com.example.electronics_store.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;
    
    private String title;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;
    
    private String type;
    
    @Column(name = "is_read")
    private Boolean isRead;
    
    @Column(name = "related_id")
    private Integer relatedId;
    
    @Column(name = "related_type")
    private String relatedType;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
