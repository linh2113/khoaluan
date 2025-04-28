package com.example.electronics_store.repository;

import com.example.electronics_store.model.Product;
import com.example.electronics_store.model.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductDetailRepository extends JpaRepository<ProductDetail, Integer> {
    Optional<ProductDetail> findByProduct(Product product);
    
    @Query("SELECT pd FROM ProductDetail pd WHERE pd.processor LIKE %:processor%")
    List<ProductDetail> findByProcessorContaining(@Param("processor") String processor);
    
    @Query("SELECT pd FROM ProductDetail pd WHERE pd.ram LIKE %:ram%")
    List<ProductDetail> findByRamContaining(@Param("ram") String ram);
    
    @Query("SELECT pd FROM ProductDetail pd WHERE pd.storage LIKE %:storage%")
    List<ProductDetail> findByStorageContaining(@Param("storage") String storage);
    
    @Query("SELECT pd FROM ProductDetail pd WHERE pd.graphics LIKE %:graphics%")
    List<ProductDetail> findByGraphicsContaining(@Param("graphics") String graphics);
    
    @Query("SELECT pd FROM ProductDetail pd WHERE pd.operatingSystem LIKE %:os%")
    List<ProductDetail> findByOperatingSystemContaining(@Param("os") String os);
}
