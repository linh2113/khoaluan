package com.example.electronics_store.repository;

import com.example.electronics_store.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>, JpaSpecificationExecutor<Category> {
    Optional<Category> findByCategoryName(String categoryName);
    List<Category> findByStatus(Integer status);
    boolean existsByCategoryName(String categoryName);
    @Query("SELECT c FROM Category c WHERE c.status = 1")
    List<Category> findAllActiveCategories();
}
