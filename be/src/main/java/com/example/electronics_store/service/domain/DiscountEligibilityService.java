package com.example.electronics_store.service.domain;

import com.example.electronics_store.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Domain service xử lý logic kiểm tra điều kiện áp dụng discount
 */
public interface DiscountEligibilityService {

    /**
     * Kiểm tra xem một sản phẩm có đủ điều kiện để áp dụng discount không
     * @param productId ID của sản phẩm cần kiểm tra
     * @return true nếu sản phẩm đủ điều kiện, false nếu không
     */
    boolean isProductEligibleForDiscount(Integer productId);

    /**
     * Kiểm tra xem một danh sách sản phẩm có đủ điều kiện để áp dụng discount không
     * @param productIds Danh sách ID sản phẩm cần kiểm tra
     * @return Danh sách các ID sản phẩm đủ điều kiện
     */
    List<Integer> getEligibleProductIds(List<Integer> productIds, LocalDateTime startDate, LocalDateTime endDate);

    boolean hasOverlappingDiscount(Integer productId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Lấy danh sách sản phẩm đủ điều kiện để áp dụng discount
     * @param search Từ khóa tìm kiếm (tùy chọn)
     * @param pageable Thông tin phân trang
     */
    Page<ProductDTO> getEligibleProducts(String search, Pageable pageable);

    /**
     * Kiểm tra xem một danh mục có đủ điều kiện để áp dụng discount không
     * @param categoryId ID của danh mục cần kiểm tra
     */
    boolean isCategoryEligibleForDiscount(Integer categoryId);
}