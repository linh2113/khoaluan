package com.example.electronics_store.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "flash_sale_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlashSaleItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "id_flash_sale", nullable = false)
    private FlashSale flashSale;
    
    @ManyToOne
    @JoinColumn(name = "id_product", nullable = false)
    private Product product;
    
    @Column(name = "flash_price", nullable = false)
    private Float flashPrice;
    
    @Column(name = "stock_limit")
    private Integer stockLimit;
    
    @Column(name = "sold_count")
    private Integer soldCount;
}
