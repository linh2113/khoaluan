package com.example.electronics_store.service;

import com.example.electronics_store.dto.BrandDTO;
import com.example.electronics_store.dto.CategoryDTO;
import com.example.electronics_store.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    
    CategoryDTO updateCategory(Integer id, CategoryDTO categoryDTO);
    
    CategoryDTO getCategoryById(Integer id);
    
    List<CategoryDTO> getAllCategories();
    
    List<CategoryDTO> getAllActiveCategories();

    Page<CategoryDTO> getCategoriesWithSearch(String search, Pageable pageable);
    boolean existsByCategoryName(String categoryName);
    
    Optional<Category> getCategoryEntityById(Integer id);
}
