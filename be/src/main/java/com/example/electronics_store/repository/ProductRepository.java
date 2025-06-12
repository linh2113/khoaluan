package com.example.electronics_store.repository;

import com.example.electronics_store.model.Brand;
import com.example.electronics_store.model.Category;
import com.example.electronics_store.model.Order;
import com.example.electronics_store.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>,JpaSpecificationExecutor<Product> {
    List<Product> findByCategory(Category category);

    List<Product> findByNameContaining(String name);
    // Tìm trong khoảng giá nào đó
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByPriceRange(@Param("minPrice") Integer minPrice, @Param("maxPrice") Integer maxPrice);

    @Query("SELECT p FROM Product p WHERE p.status = true")
    List<Product> findAllActiveProducts();

    @Query("SELECT p FROM Product p WHERE p.status = true")
    Page<Product> findAllActiveProducts(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.stock > 0 AND p.status = true")
    List<Product> findAllInStockProducts();

    @Query("SELECT p FROM Product p WHERE p.stock < 10 AND p.status = true")
    List<Product> findLowStockProducts();
    @Query("SELECT p FROM Product p WHERE p.brand = :brand AND p.status = true")
    List<Product> findByBrandAndStatusTrue(@Param("brand")Brand brand);

    @Query(value = "SELECT p.* FROM products p " +
            "JOIN (SELECT id_product, COUNT(*) as order_count FROM order_details GROUP BY id_product ORDER BY order_count DESC LIMIT 10) od " +
            "ON p.id = od.id_product WHERE p.status = true", nativeQuery = true)
    List<Product> findTopSellingProducts();

    @Query(value = "SELECT p.* FROM products p " +
            "JOIN (SELECT id_product, AVG(rating) as avg_rating FROM rating GROUP BY id_product ORDER BY avg_rating DESC LIMIT 10) r " +
            "ON p.id = r.id_product WHERE p.status = 1", nativeQuery = true)
    List<Product> findTopRatedProducts();

    @Query(value = """
        SELECT p.* FROM products p
        LEFT JOIN order_details od ON p.id = od.id_product
        WHERE p.status = true
        GROUP BY p.id
        ORDER BY COUNT(od.id) DESC
        """,
            countQuery = "SELECT COUNT(DISTINCT p.id) FROM products p LEFT JOIN order_details od ON p.id = od.id_product WHERE p.status = true",
            nativeQuery = true)
    Page<Product> findTopSellingProducts(Pageable pageable);
    @Query(value = """
        SELECT p.* FROM products p
        LEFT JOIN rating r ON p.id = r.id_product
        WHERE p.status = true
        GROUP BY p.id
        ORDER BY AVG(r.rating) DESC
        """,
            countQuery = "SELECT COUNT(*) FROM products p WHERE p.status = true",
            nativeQuery = true)
    Page<Product> findTopRatedProducts(Pageable pageable);

    @Query(value = """
    SELECT p.* FROM products p
    JOIN flash_sale_item fsi ON p.id = fsi.product_id
    JOIN flash_sale fs ON fsi.flash_sale_id = fs.id
    WHERE p.status = true
    AND fs.start_time <= :now
    AND fs.end_time >= :now
    """,
            countQuery = """
    SELECT COUNT(p.id) FROM products p
    JOIN flash_sale_item fsi ON p.id = fsi.product_id
    JOIN flash_sale fs ON fsi.flash_sale_id = fs.id
    WHERE p.status = true
    AND fs.start_time <= :now
    AND fs.end_time >= :now
    """,
            nativeQuery = true)
    Page<Product> findActiveFlashSaleProducts(@Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.status = true ORDER BY p.createAt DESC")
    Page<Product> findByOrderByCreateAtDesc(Pageable pageable);
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);

    // Update sold quantity
    @Modifying
    @Query("UPDATE Product p SET p.soldQuantity = p.soldQuantity + :quantity WHERE p.id = :productId")
    void updateSoldQuantity(@Param("productId") Integer productId, @Param("quantity") Integer quantity);

    // Recalculate sold quantity from order details (for data consistency)
    @Modifying
    @Query("UPDATE Product p SET p.soldQuantity = " +
           "(SELECT COALESCE(SUM(od.quantity), 0) FROM OrderDetail od " +
           "JOIN od.order o WHERE od.product.id = p.id AND o.orderStatus = 4) " +
           "WHERE p.id = :productId")
    void recalculateSoldQuantity(@Param("productId") Integer productId);

    // Batch recalculate all products sold quantity
    @Modifying
    @Query("UPDATE Product p SET p.soldQuantity = " +
           "(SELECT COALESCE(SUM(od.quantity), 0) FROM OrderDetail od " +
           "JOIN od.order o WHERE od.product.id = p.id AND o.orderStatus = 4)")
    void recalculateAllSoldQuantities();

    // Get products ordered by sold quantity (best sellers)
    @Query("SELECT p FROM Product p WHERE p.status = true ORDER BY p.soldQuantity DESC")
    Page<Product> findBestSellingProducts(Pageable pageable);

    @Query("SELECT DISTINCT pd.product.id FROM ProductDiscount pd " +
       "WHERE pd.discount.isActive = true " +
       "AND pd.discount.startDate <= :now " +
       "AND pd.discount.endDate >= :now")
List<Integer> findProductIdsWithActiveDiscounts(@Param("now") LocalDateTime now);
}
