package com.example.electronics_store.repository;

import com.example.electronics_store.model.FlashSaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlashSaleItemRepository extends JpaRepository<FlashSaleItem, Integer> {
    @Query("SELECT fsi FROM FlashSaleItem fsi WHERE fsi.product.id = :productId AND " +
           "fsi.flashSale.startTime <= :now AND fsi.flashSale.endTime >= :now")
    Optional<FlashSaleItem> findActiveFlashSaleItemByProductId(@Param("productId") Integer productId, @Param("now") LocalDateTime now);

    List<FlashSaleItem> findByFlashSaleId(Integer flashSaleId);
    @Query("SELECT fsi FROM FlashSaleItem fsi WHERE fsi.product.id = :productId AND " +
            "fsi.flashSale.startTime <= :orderTime AND fsi.flashSale.endTime >= :orderTime")
    Optional<FlashSaleItem> findByProductAndTimeRange(@Param("productId") Integer productId,
                                                      @Param("orderTime") LocalDateTime orderTime);


}