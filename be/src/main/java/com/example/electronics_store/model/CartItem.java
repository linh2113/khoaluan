package com.example.electronics_store.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "selected", nullable = false)
    private Boolean selected = false;
    @ManyToOne
    @JoinColumn(name = "id_product", nullable = false)
    private Product product;
    
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cart")
    private Cart cart;
    
    private Integer quantity;
    
    private Float price;
    
    @Column(name = "create_at", updatable = false)
    private LocalDateTime createAt;
    
    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
    }
}
