package com.example.electronics_store.service;

import com.example.electronics_store.dto.BrandDTO;
import com.example.electronics_store.model.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BrandService {
    BrandDTO createBrand(BrandDTO brandDTO);

    BrandDTO updateBrand(Integer id, BrandDTO brandDTO);

    BrandDTO getBrandById(Integer id);

    Page<BrandDTO> getAllBrands(Pageable pageable);

    List<BrandDTO> getAllActiveBrands();

    void deleteBrand(Integer id);

    List<BrandDTO> searchBrands(String keyword);

    Optional<Brand> getBrandEntityById(Integer id);
    Page<BrandDTO> getBrandsWithSearch(String search, Pageable pageable);
    Optional<Brand> getBrandEntityByName(String brandName);
}