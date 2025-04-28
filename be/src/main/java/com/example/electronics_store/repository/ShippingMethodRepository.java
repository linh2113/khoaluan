package com.example.electronics_store.repository;


import com.example.electronics_store.model.ShippingMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShippingMethodRepository extends JpaRepository<ShippingMethod, Integer> {
    List<ShippingMethod> findByIsActive(Boolean isActive);
    boolean existsByMethodName(String methodName);
}
