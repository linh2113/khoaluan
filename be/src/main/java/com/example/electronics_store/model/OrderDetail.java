package com.example.electronics_store.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "id_order")
    private Order order;
    
    @ManyToOne
    @JoinColumn(name = "id_product")
    private Product product;
    
    private Integer quantity;
    
    @Column(name = "review_status")
    private Boolean reviewStatus;
    private Float price;
}
