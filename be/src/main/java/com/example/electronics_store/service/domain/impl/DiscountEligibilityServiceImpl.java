package com.example.electronics_store.service.domain.impl;

import com.example.electronics_store.dto.ProductDTO;
import com.example.electronics_store.model.*;
import com.example.electronics_store.repository.*;
import com.example.electronics_store.service.domain.DiscountEligibilityService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiscountEligibilityServiceImpl implements DiscountEligibilityService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FlashSaleItemRepository flashSaleItemRepository;

    @Autowired
    private ProductDiscountRepository productDiscountRepository;

    @Autowired
    private CategoryDiscountRepository categoryDiscountRepository;

    @Override
    public boolean isProductEligibleForDiscount(Integer productId) {
        LocalDateTime now = LocalDateTime.now();

        // Kiểm tra sản phẩm có tồn tại không
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Kiểm tra sản phẩm có trong flash sale không
        boolean inFlashSale = flashSaleItemRepository.findActiveFlashSaleItemByProductId(productId, now).isPresent();
        if (inFlashSale) {
            return false;
        }

        // Kiểm tra sản phẩm đã có discount chưa
        boolean hasProductDiscount = !productDiscountRepository.findEffectiveDiscountsByProduct(product, now).isEmpty();
        if (hasProductDiscount) {
            return false;
        }


        return true;
    }

    @Override
    public boolean hasOverlappingDiscount(Integer productId, LocalDateTime startDate, LocalDateTime endDate) {
        return !productDiscountRepository.findByProductAndTimeOverlap(productId, startDate, endDate).isEmpty();
    }

     @Override
    public List<Integer> getEligibleProductIds(List<Integer> productIds, LocalDateTime startDate, LocalDateTime endDate) {
        return productIds.stream()
                .filter(productId -> !hasOverlappingDiscount(productId, startDate, endDate))
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductDTO> getEligibleProducts(String search, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();

        // Tạo specification để lọc sản phẩm
        Specification<Product> spec = createEligibilitySpecification(search, now);

        // Thực hiện truy vấn với specification và phân trang
        return productRepository.findAll(spec, pageable)
                .map(this::mapProductToDTO);
    }

    
    @Override
    public boolean isCategoryEligibleForDiscount(Integer categoryId, LocalDateTime startDate, LocalDateTime endDate) {
    return categoryDiscountRepository.findByCategoryAndTimeOverlap(categoryId, startDate, endDate).isEmpty();
    }

    /**
     * Tạo specification để lọc sản phẩm đủ điều kiện áp dụng discount
     */
    private Specification<Product> createEligibilitySpecification(String search, LocalDateTime now) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Chỉ lấy sản phẩm active
            predicates.add(cb.isTrue(root.get("status")));

            // Tìm kiếm theo keyword nếu có
            if (StringUtils.hasText(search)) {
                String searchTerm = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), searchTerm),
                        cb.like(cb.lower(root.get("description")), searchTerm)
                ));
            }

            // Subquery để kiểm tra sản phẩm không có trong flash sale
            Subquery<Integer> flashSaleSubquery = query.subquery(Integer.class);
            Root<FlashSaleItem> flashSaleItemRoot = flashSaleSubquery.from(FlashSaleItem.class);
            Join<FlashSaleItem, FlashSale> flashSaleJoin = flashSaleItemRoot.join("flashSale");

            flashSaleSubquery.select(flashSaleItemRoot.get("product").get("id"))
                    .where(
                            cb.equal(flashSaleItemRoot.get("product").get("id"), root.get("id")),
                            cb.lessThanOrEqualTo(flashSaleJoin.get("startTime"), now),
                            cb.greaterThanOrEqualTo(flashSaleJoin.get("endTime"), now)
                    );

            // Sản phẩm không nằm trong flash sale hiện tại
            predicates.add(cb.not(cb.exists(flashSaleSubquery)));

            // Subquery để kiểm tra sản phẩm không có product discount
            Subquery<Integer> productDiscountSubquery = query.subquery(Integer.class);
            Root<ProductDiscount> productDiscountRoot = productDiscountSubquery.from(ProductDiscount.class);
            Join<ProductDiscount, Discount> discountJoin = productDiscountRoot.join("discount");

            productDiscountSubquery.select(productDiscountRoot.get("product").get("id"))
                    .where(
                            cb.equal(productDiscountRoot.get("product").get("id"), root.get("id")),
                            cb.lessThanOrEqualTo(discountJoin.get("startDate"), now),
                            cb.greaterThanOrEqualTo(discountJoin.get("endDate"), now),
                            cb.isTrue(discountJoin.get("isActive"))
                    );

            // Sản phẩm không có product discount hiện tại
            predicates.add(cb.not(cb.exists(productDiscountSubquery)));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Chuyển đổi Product entity thành ProductDTO
     */
    private ProductDTO mapProductToDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .image(product.getImage())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getCategoryName())
                .stock(product.getStock())
                .status(product.getStatus())
                .build();
    }
}