package com.example.electronics_store.repository;

import com.example.electronics_store.model.FlashSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlashSaleRepository extends JpaRepository<FlashSale, Integer>, JpaSpecificationExecutor<FlashSale> {
    
    // Tìm flash sale đang hoạt động
    @Query("SELECT fs FROM FlashSale fs WHERE fs.startTime <= :now AND fs.endTime >= :now")
    List<FlashSale> findCurrentFlashSales(@Param("now") LocalDateTime now);
    
    // Tìm flash sale sắp tới
    @Query("SELECT fs FROM FlashSale fs WHERE fs.startTime > :now ORDER BY fs.startTime ASC")
    List<FlashSale> findUpcomingFlashSales(@Param("now") LocalDateTime now);
    
    // Tìm flash sale đã kết thúc
    @Query("SELECT fs FROM FlashSale fs WHERE fs.endTime < :now ORDER BY fs.endTime DESC")
    List<FlashSale> findPastFlashSales(@Param("now") LocalDateTime now);
    
    // Kiểm tra xem có flash sale nào đang hoạt động trong khoảng thời gian không
    @Query("SELECT COUNT(fs) > 0 FROM FlashSale fs WHERE " +
           "(fs.startTime <= :endTime AND fs.endTime >= :startTime)")
    boolean existsActiveFlashSaleInTimeRange(@Param("startTime") LocalDateTime startTime, 
                                           @Param("endTime") LocalDateTime endTime);
    
    // Tìm flash sale theo tên
    @Query("SELECT fs FROM FlashSale fs WHERE LOWER(fs.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<FlashSale> findByNameContainingIgnoreCase(@Param("name") String name);
    
    // Tìm flash sale có chứa sản phẩm cụ thể
    @Query("SELECT DISTINCT fs FROM FlashSale fs JOIN fs.flashSaleItems fsi WHERE fsi.product.id = :productId")
    List<FlashSale> findFlashSalesByProductId(@Param("productId") Integer productId);
    
    // Tìm flash sale đang hoạt động có chứa sản phẩm cụ thể
    @Query("SELECT fs FROM FlashSale fs JOIN fs.flashSaleItems fsi WHERE " +
           "fsi.product.id = :productId AND fs.startTime <= :now AND fs.endTime >= :now")
    Optional<FlashSale> findActiveFlashSaleByProductId(@Param("productId") Integer productId, 
                                                      @Param("now") LocalDateTime now);
    
    // Đếm số lượng flash sale đang hoạt động
    @Query("SELECT COUNT(fs) FROM FlashSale fs WHERE fs.startTime <= :now AND fs.endTime >= :now")
    Long countActiveFlashSales(@Param("now") LocalDateTime now);
    
    // Tìm flash sale theo khoảng thời gian
    @Query("SELECT fs FROM FlashSale fs WHERE fs.startTime >= :startDate AND fs.endTime <= :endDate")
    List<FlashSale> findFlashSalesInDateRange(@Param("startDate") LocalDateTime startDate, 
                                            @Param("endDate") LocalDateTime endDate);
}
