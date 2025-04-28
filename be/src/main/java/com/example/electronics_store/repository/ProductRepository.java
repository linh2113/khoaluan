package com.example.electronics_store.repository;

import com.example.electronics_store.model.Category;
import com.example.electronics_store.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
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
    List<Product> findByBrand(@Param("brand") String brand);

    @Query(value = "SELECT p.* FROM products p " +
            "JOIN (SELECT id_product, COUNT(*) as order_count FROM order_details GROUP BY id_product ORDER BY order_count DESC LIMIT 10) od " +
            "ON p.id = od.id_product WHERE p.status = 1", nativeQuery = true)
    List<Product> findTopSellingProducts();
    
    @Query(value = "SELECT p.* FROM products p " +
            "JOIN (SELECT product_id, AVG(rating) as avg_rating FROM rating GROUP BY product_id ORDER BY avg_rating DESC LIMIT 10) r " +
            "ON p.id = r.product_id WHERE p.status = 1", nativeQuery = true)
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
        LEFT JOIN ratings r ON p.id = r.product_id 
        WHERE p.status = true 
        GROUP BY p.id 
        ORDER BY AVG(r.rating) DESC
        """,
            countQuery = "SELECT COUNT(*) FROM products p WHERE p.status = true",
            nativeQuery = true)
    Page<Product> findTopRatedProducts(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.discount IS NOT NULL AND p.status = true")
    Page<Product> findDiscountedProducts(Pageable pageable);
    @Query("SELECT p FROM Product p WHERE p.status = true ORDER BY p.createAt DESC")
    Page<Product> findByOrderByCreateAtDesc(Pageable pageable);
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);
}
