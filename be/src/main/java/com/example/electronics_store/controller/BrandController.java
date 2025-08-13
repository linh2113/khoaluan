package com.example.electronics_store.controller;

import com.example.electronics_store.dto.ApiResponse;
import com.example.electronics_store.dto.BrandDTO;
import com.example.electronics_store.service.BrandService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brands")
@CrossOrigin(origins = "*")
public class BrandController {

    private final BrandService brandService;

    @Autowired
    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllActiveBrands() {
        List<BrandDTO> brands = brandService.getAllActiveBrands();
        return ResponseEntity.ok(ApiResponse.success(brands));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getBrandById(@PathVariable Integer id) {
        BrandDTO brand = brandService.getBrandById(id);
        return ResponseEntity.ok(ApiResponse.success(brand));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<?>> searchBrands(@RequestParam String keyword) {
        List<BrandDTO> brands = brandService.searchBrands(keyword);
        return ResponseEntity.ok(ApiResponse.success(brands));
    }

}