package com.example.electronics_store.repository;

import com.example.electronics_store.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer>, JpaSpecificationExecutor<Brand> {
    Optional<Brand> findByBrandName(String brandName);

    List<Brand> findByBrandNameContaining(String brandName);

    @Query("SELECT b FROM Brand b WHERE b.status = 1")
    List<Brand> findAllActiveBrands();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.brand.id = :brandId")
    Long countProductsByBrand(@Param("brandId") Integer brandId);

    boolean existsByBrandName(String brandName);
}