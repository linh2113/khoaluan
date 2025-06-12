package com.example.electronics_store.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;
    
    @Column(name = "total_price")
    private Float totalPrice;
    
    @Column(name = "shipping_fee")
    private Float shippingFee;
    
    private String address;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @ManyToOne
    @JoinColumn(name = "id_shipping_method")
    private ShippingMethod shippingMethod;
    
    @ManyToOne
    @JoinColumn(name = "id_payment_method")
    private PaymentMethod paymentMethod;
    
    @Column(name = "tracking_number")
    private String trackingNumber;
    
    @Column(name = "create_at", updatable = false)
    private LocalDateTime createAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "payment_status")
    private String paymentStatus;
    
    @Column(name = "order_status")
    private Integer orderStatus;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails;
    
    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
