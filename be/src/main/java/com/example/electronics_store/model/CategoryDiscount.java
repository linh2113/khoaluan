package com.example.electronics_store.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "category_discounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDiscount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "id_category", nullable = false)
    private Category category;
    
    @ManyToOne
    @JoinColumn(name = "id_discount", nullable = false)
    private Discount discount;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}