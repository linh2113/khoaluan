package com.example.electronics_store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchAssignRequest {
    private List<Integer> productIds;
    private Map<Integer, Integer> discountedPrices; // Optional: productId -> discountedPrice
}