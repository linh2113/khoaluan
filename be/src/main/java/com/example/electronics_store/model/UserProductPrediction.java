package com.example.electronics_store.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_product_predictions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProductPrediction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    private Integer rating;
    
    @Column(name = "predicted_at", updatable = false)
    private LocalDateTime predictedAt;
    
    @PrePersist
    protected void onCreate() {
        predictedAt = LocalDateTime.now();
    }
}
