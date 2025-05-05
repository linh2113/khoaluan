package com.example.electronics_store.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "id_category")
    private Category category;
    
    @ManyToOne
    @JoinColumn(name = "id_discount")
    private Discount discount;
    
    private String name;

    @ManyToOne
    @JoinColumn(name = "id_brand")
    private Brand brand;
    
    private String image;
    
    private Integer price;
    
    @Column(name = "des", columnDefinition = "TEXT")
    private String description;
    
    private String warranty;
    
    private Float weight;
    
    private String dimensions;
    
    @Column(name = "create_at", updatable = false)
    private LocalDateTime createAt;
    
    @Column(name = "create_by")
    private String createBy;
    
    private Boolean status;
    
    @Column(name = "update_at")
    private LocalDateTime updateAt;
    
    @Column(name = "update_by")
    private String updateBy;
    
    private Integer stock;
    
    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }
}
