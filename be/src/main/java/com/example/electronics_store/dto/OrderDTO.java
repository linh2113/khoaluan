package com.example.electronics_store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Integer id;
    private Integer userId;
    private String userName;
    private Float totalPrice;
    private Float shippingFee;
    private String address;
    private String phoneNumber;
    private Integer shippingMethodId;
    private String shippingMethodName;
    private Integer paymentMethodId;
    private String paymentMethodName;
    private String trackingNumber;
    private LocalDateTime createAt;
    private String paymentStatus;
    private Integer orderStatus;
    private String orderStatusText;
    private List<OrderDetailDTO> orderDetails;
}
