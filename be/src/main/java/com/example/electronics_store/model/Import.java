package com.example.electronics_store.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "imports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Import {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "id_product")
    private Product product;
    
    private Integer quantity;
    
    @Column(name = "date_import", updatable = false)
    private LocalDateTime dateImport;
    
    private Float price;
    
    private String status;
    
    @PrePersist
    protected void onCreate() {
        dateImport = LocalDateTime.now();
    }
}
