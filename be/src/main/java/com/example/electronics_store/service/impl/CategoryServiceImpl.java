package com.example.electronics_store.service.impl;

import com.example.electronics_store.dto.CategoryDTO;
import com.example.electronics_store.model.Category;
import com.example.electronics_store.repository.CategoryRepository;
import com.example.electronics_store.service.CategoryService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryDTO> getCategoriesWithSearch(String search, Pageable pageable) {
        Specification<Category> spec = Specification.where(null);
        if (search != null && !search.trim().isEmpty()) {
            String searchTerm = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                try {
                    Integer categoryId = Integer.parseInt(search);
                    predicates.add(cb.equal(root.get("id"), categoryId));
                } catch (NumberFormatException ignored) {
                }
                predicates.add(cb.like(cb.lower(root.get("categoryName")), searchTerm));
                try {
                    Integer status = Integer.parseInt(search);
                    if (status == 0 || status == 1) {
                        predicates.add(cb.equal(root.get("status"), status));
                    }
                } catch (NumberFormatException ignored) {
                }
                if ("active".equalsIgnoreCase(search) || "1".equals(search)) {
                    predicates.add(cb.equal(root.get("status"), 1));
                } else if ("inactive".equalsIgnoreCase(search) || "0".equals(search)) {
                    predicates.add(cb.equal(root.get("status"), 0));
                }
                if (search.matches("\\d{4}-\\d{2}(-\\d{2})?")) {
                    predicates.add(cb.like(
                            cb.function("DATE_FORMAT", String.class, root.get("createdAt"), cb.literal("%Y-%m-%d")),
                            search + "%"
                    ));
                    predicates.add(cb.like(
                            cb.function("DATE_FORMAT", String.class, root.get("updatedAt"), cb.literal("%Y-%m-%d")),
                            search + "%"
                    ));
                }
                return cb.or(predicates.toArray(new Predicate[0]));
            });
        }

        Page<Category> categoryPage = categoryRepository.findAll(spec, pageable);
        return categoryPage.map(this::mapCategoryToDTO);
    }

    // Helper method
    private CategoryDTO mapCategoryToDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .categoryName(category.getCategoryName())
                .status(category.getStatus())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
