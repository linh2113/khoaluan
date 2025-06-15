package com.example.electronics_store.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.electronics_store.config.CloudinaryConfig;
import com.example.electronics_store.dto.BrandDTO;
import com.example.electronics_store.model.Brand;
import com.example.electronics_store.repository.BrandRepository;
import com.example.electronics_store.service.BrandService;
import com.example.electronics_store.util.CloudinaryUtils;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final Cloudinary cloudinary;
    @Autowired
    public BrandServiceImpl(BrandRepository brandRepository, Cloudinary cloudinary) {
        this.brandRepository = brandRepository;
        this.cloudinary = cloudinary;
    }

    @Override
    @Transactional
    public BrandDTO createBrand(BrandDTO brandDTO) {
        // Check if brand name already exists
        if (brandRepository.existsByBrandName(brandDTO.getBrandName())) {
            throw new RuntimeException("Brand name already exists");
        }
        // Upload ảnh nếu có
        if (brandDTO.getImageFile() != null && !brandDTO.getImageFile().isEmpty()) {
            try {
                String imageUrl = CloudinaryUtils.uploadImage(cloudinary, brandDTO.getImageFile(), "brands");
                brandDTO.setImageUrl(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image: " + e.getMessage());
            }
        }
        Brand brand = new Brand();
        brand.setBrandName(brandDTO.getBrandName());
        brand.setDescription(brandDTO.getDescription());
        brand.setImageUrl(brandDTO.getImageUrl());
        brand.setStatus(brandDTO.getStatus() != null ? brandDTO.getStatus() : true);

        Brand savedBrand = brandRepository.save(brand);
        return mapBrandToDTO(savedBrand);
    }

    @Override
    @Transactional
    public BrandDTO updateBrand(Integer id, BrandDTO brandDTO) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        // Chỉ cập nhật brandName nếu có giá trị mới và khác giá trị hiện tại
        if (brandDTO.getBrandName() != null && !brandDTO.getBrandName().equals(brand.getBrandName())) {
            Optional<Brand> existingBrand = brandRepository.findByBrandName(brandDTO.getBrandName());
            if (existingBrand.isPresent() && !existingBrand.get().getId().equals(id)) {
                throw new RuntimeException("Brand name already exists");
            }
            brand.setBrandName(brandDTO.getBrandName());
        }
        if (brandDTO.getDescription() != null) {
            brand.setDescription(brandDTO.getDescription());
        }
        // Upload ảnh mới nếu có
        if (brandDTO.getImageFile() != null && !brandDTO.getImageFile().isEmpty()) {
            try {
                String imageUrl = CloudinaryUtils.replaceImage(
                        cloudinary,
                        brandDTO.getImageFile(),
                        brand.getImageUrl(),
                        "brands"
                );
                brand.setImageUrl(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image: " + e.getMessage());
            }
        } else if (brandDTO.getImageUrl() != null) {
            brand.setImageUrl(brandDTO.getImageUrl());
        }

        if (brandDTO.getStatus() != null) {
            brand.setStatus(brandDTO.getStatus());
        }

        Brand updatedBrand = brandRepository.save(brand);
        return mapBrandToDTO(updatedBrand);
    }

    @Override
    public BrandDTO getBrandById(Integer id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found"));
        return mapBrandToDTO(brand);
    }

    @Override
    public Page<BrandDTO> getAllBrands(Pageable pageable) {
        return brandRepository.findAll(pageable)
                .map(this::mapBrandToDTO);
    }



    @Override
    public List<BrandDTO> getAllActiveBrands() {
        return brandRepository.findAllActiveBrands().stream()
                .map(this::mapBrandToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteBrand(Integer id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        // Check if brand has products
        Long productCount = brandRepository.countProductsByBrand(id);
        if (productCount > 0) {
            throw new RuntimeException("Cannot delete brand with associated products");
        }

        brandRepository.delete(brand);
    }

    @Override
    public List<BrandDTO> searchBrands(String keyword) {
        return brandRepository.findByBrandNameContaining(keyword).stream()
                .map(this::mapBrandToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Brand> getBrandEntityById(Integer id) {
        return brandRepository.findById(id);
    }

    @Override
    public Optional<Brand> getBrandEntityByName(String brandName) {
        return brandRepository.findByBrandName(brandName);
    }
    @Override
    public Page<BrandDTO> getBrandsWithSearch(String search, Pageable pageable) {
        Specification<Brand> spec = Specification.where(null);
        if (search != null && !search.trim().isEmpty()) {
            String searchTerm = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                try {
                    Integer brandId = Integer.parseInt(search);
                    predicates.add(cb.equal(root.get("id"), brandId));
                } catch (NumberFormatException ignored) {
                }
                predicates.add(cb.like(cb.lower(root.get("brandName")), searchTerm));
                predicates.add(cb.like(cb.lower(root.get("description")), searchTerm));
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
        Page<Brand> brandPage = brandRepository.findAll(spec, pageable);
        return brandPage.map(this::mapBrandToDTO);
    }

    // Helper method to map Brand entity to BrandDTO
    private BrandDTO mapBrandToDTO(Brand brand) {
        Long productCount = brandRepository.countProductsByBrand(brand.getId());

        return BrandDTO.builder()
                .id(brand.getId())
                .brandName(brand.getBrandName())
                .description(brand.getDescription())
                .imageUrl(brand.getImageUrl())
                .createdAt(brand.getCreatedAt())
                .updatedAt(brand.getUpdatedAt())
                .status(brand.getStatus())
                .productCount(productCount)
                .build();
    }
}