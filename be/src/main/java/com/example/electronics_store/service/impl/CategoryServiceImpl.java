package com.example.electronics_store.service.impl;

import com.example.electronics_store.dto.CategoryDTO;
import com.example.electronics_store.model.Category;
import com.example.electronics_store.repository.CategoryRepository;
import com.example.electronics_store.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        if (categoryRepository.existsByCategoryName(categoryDTO.getCategoryName())) {
            throw new RuntimeException("Category name already exists");
        }

        Category category = new Category();
        category.setCategoryName(categoryDTO.getCategoryName());
        category.setStatus(categoryDTO.getStatus());

        Category savedCategory = categoryRepository.save(category);
        return mapCategoryToDTO(savedCategory);
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(Integer id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Check if categoryName is being updated and if new name exists
        if (categoryDTO.getCategoryName() != null) {
            if (!category.getCategoryName().equals(categoryDTO.getCategoryName()) &&
                    categoryRepository.existsByCategoryName(categoryDTO.getCategoryName())) {
                throw new RuntimeException("Category name already exists");
            }
            category.setCategoryName(categoryDTO.getCategoryName());
        }

        // Update status only if it's not null
        if (categoryDTO.getStatus() != null) {
            category.setStatus(categoryDTO.getStatus());
        }

        Category updatedCategory = categoryRepository.save(category);
        return mapCategoryToDTO(updatedCategory);
    }

    @Override
    public CategoryDTO getCategoryById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return mapCategoryToDTO(category);
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapCategoryToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDTO> getAllActiveCategories() {
        return categoryRepository.findAllActiveCategories().stream()
                .map(this::mapCategoryToDTO)
                .collect(Collectors.toList());
    }


    @Override
    public boolean existsByCategoryName(String categoryName) {
        return categoryRepository.existsByCategoryName(categoryName);
    }

    @Override
    public Optional<Category> getCategoryEntityById(Integer id) {
        return categoryRepository.findById(id);
    }

    // Helper method
    private CategoryDTO mapCategoryToDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .categoryName(category.getCategoryName())
                .status(category.getStatus())
                .build();
    }
}
