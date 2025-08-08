package com.example.electronics_store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    private boolean success;
    private Integer orderId;
    private Long amount;
    private String transactionId;
    private String bankCode;
    private String paymentDate;
    private String message;
}