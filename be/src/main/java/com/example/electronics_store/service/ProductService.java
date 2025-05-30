package com.example.electronics_store.service;

import com.example.electronics_store.dto.*;
import com.example.electronics_store.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    ProductDTO createProduct(ProductCreateDTO productCreateDTO);
    
    ProductDTO updateProduct(Integer id, ProductUpdateDTO productUpdateDTO);
    
    ProductDTO getProductById(Integer id);

    //for user
    Page<ProductDTO> getAllProducts(ProductFilterRequest filterRequest);
    //for admin
    Page<ProductDTO> getAllProducts(Pageable pageable);
    Page<ProductDTO> getAllActiveProducts(Pageable pageable);

    List<ProductDTO> getProductsByCategory(Integer categoryId);

    List<ProductDTO> searchProducts(String keyword);

    List<ProductDTO> getProductsByPriceRange(Integer minPrice, Integer maxPrice);

    List<ProductDTO> getProductsByBrand(String brand);

    List<ProductDTO> getTopSellingProducts();

    List<ProductDTO> getTopRatedProducts();
    
    List<ProductDTO> getRecommendedProducts(Integer userId);



    List<ProductDTO> getNewArrivals();

    
    List<ProductDTO> getLowStockProducts();

    
    void updateProductStock(Integer id, Integer quantity);
    
    String uploadProductImage(Integer productId, MultipartFile file, boolean isPrimary);

    void deleteProductImage(Integer imageId);
    void updatePrimaryImage(Integer imageId);
    ProductDetailDTO getProductDetail(Integer productId);
    
    ProductDetailDTO updateProductDetail(Integer productId, ProductDetailDTO productDetailDTO);
    
    List<String> getAllBrands();
    
    Optional<Product> getProductEntityById(Integer id);
    
    List<ProductDTO> compareProducts(List<Integer> productIds);

}
