package com.example.electronics_store.repository;

import com.example.electronics_store.model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Integer> {
    Optional<Discount> findByCode(String code);
    
    @Query("SELECT d FROM Discount d WHERE d.startDate <= :now AND d.endDate >= :now AND d.quantity > 0")
    List<Discount> findAllActiveDiscounts(@Param("now") LocalDateTime now);
    
    @Query("SELECT d FROM Discount d WHERE d.endDate < :now")
    List<Discount> findAllExpiredDiscounts(@Param("now") LocalDateTime now);
    
    @Query("SELECT d FROM Discount d WHERE d.startDate > :now")
    List<Discount> findAllUpcomingDiscounts(@Param("now") LocalDateTime now);
}
