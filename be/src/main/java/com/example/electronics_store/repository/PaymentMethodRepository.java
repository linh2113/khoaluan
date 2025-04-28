package com.example.electronics_store.repository;


import com.example.electronics_store.model.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;



@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Integer> {
    List<PaymentMethod> findByIsActive(Boolean isActive);

    boolean existsByMethodName(String methodName);
}
