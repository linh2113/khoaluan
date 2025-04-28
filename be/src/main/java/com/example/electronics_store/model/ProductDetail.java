package com.example.electronics_store.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @OneToOne
    @JoinColumn(name = "id_product", nullable = false)
    private Product product;
    
    private String processor;
    
    private String ram;
    
    private String storage;
    
    private String display;
    
    private String graphics;
    
    private String battery;
    
    private String camera;
    
    @Column(name = "operating_system")
    private String operatingSystem;
    
    private String connectivity;
    
    @Column(name = "other_features", columnDefinition = "TEXT")
    private String otherFeatures;
}
