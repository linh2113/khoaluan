package com.example.electronics_store.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.electronics_store.dto.*;
import com.example.electronics_store.model.*;
import com.example.electronics_store.repository.*;
import com.example.electronics_store.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final DiscountRepository discountRepository;
    private final ProductDetailRepository productDetailRepository;
    private final ProductImageRepository productImageRepository;
    private final RatingRepository ratingRepository;
    private final BrandRepository brandRepository;
    @Autowired
    private Cloudinary cloudinary;
    @Value("${app.upload.dir:${user.home}/techstore/uploads}")
    private String uploadDir;
    
    @Autowired
    public ProductServiceImpl(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            DiscountRepository discountRepository,
            ProductDetailRepository productDetailRepository,
            ProductImageRepository productImageRepository,
            RatingRepository ratingRepository, BrandRepository brandRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.discountRepository = discountRepository;
        this.productDetailRepository = productDetailRepository;
        this.productImageRepository = productImageRepository;
        this.ratingRepository = ratingRepository;
        this.brandRepository = brandRepository;
    }

    @Override
    @Transactional
    public ProductDTO createProduct(ProductCreateDTO productCreateDTO) {
        // Get category
        Category category = categoryRepository.findById(productCreateDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Get discount if provided
        Discount discount = null;
        if (productCreateDTO.getDiscountId() != null) {
            discount = discountRepository.findById(productCreateDTO.getDiscountId())
                    .orElseThrow(() -> new RuntimeException("Discount not found"));
        }
        // Get brand if provided
        Brand brand = null;
        if (productCreateDTO.getBrandId() != null) {
            brand = brandRepository.findById(productCreateDTO.getBrandId())
                    .orElseThrow(() -> new RuntimeException("Brand not found"));
        }
        // Create product
        Product product = new Product();
        product.setCategory(category);
        product.setDiscount(discount);
        product.setName(productCreateDTO.getName());
        product.setBrand(brand);
        product.setPrice(productCreateDTO.getPrice());
        product.setDescription(productCreateDTO.getDescription());
        product.setWarranty(productCreateDTO.getWarranty());
        product.setWeight(productCreateDTO.getWeight());
        product.setDimensions(productCreateDTO.getDimensions());
        product.setStatus(productCreateDTO.getStatus());
        product.setStock(productCreateDTO.getStock());
        product.setCreateBy("admin"); // This should be the current user

        Product savedProduct = productRepository.save(product);

        // Create product detail if provided
        if (productCreateDTO.getProcessor() != null || productCreateDTO.getRam() != null ||
                productCreateDTO.getStorage() != null || productCreateDTO.getDisplay() != null ||
                productCreateDTO.getGraphics() != null || productCreateDTO.getBattery() != null ||
                productCreateDTO.getCamera() != null || productCreateDTO.getOperatingSystem() != null ||
                productCreateDTO.getConnectivity() != null || productCreateDTO.getOtherFeatures() != null) {

            ProductDetail productDetail = new ProductDetail();
            productDetail.setProduct(savedProduct);
            productDetail.setProcessor(productCreateDTO.getProcessor());
            productDetail.setRam(productCreateDTO.getRam());
            productDetail.setStorage(productCreateDTO.getStorage());
            productDetail.setDisplay(productCreateDTO.getDisplay());
            productDetail.setGraphics(productCreateDTO.getGraphics());
            productDetail.setBattery(productCreateDTO.getBattery());
            productDetail.setCamera(productCreateDTO.getCamera());
            productDetail.setOperatingSystem(productCreateDTO.getOperatingSystem());
            productDetail.setConnectivity(productCreateDTO.getConnectivity());
            productDetail.setOtherFeatures(productCreateDTO.getOtherFeatures());

            productDetailRepository.save(productDetail);
        }

        return mapProductToDTO(savedProduct);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Integer id, ProductUpdateDTO productUpdateDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Update category only if provided
        if (productUpdateDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(productUpdateDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            product.setCategory(category);
        }

        // Update discount - set null if discountId is null, update if provided
        if (productUpdateDTO.getDiscountId() != null) {
            Discount discount = discountRepository.findById(productUpdateDTO.getDiscountId())
                    .orElseThrow(() -> new RuntimeException("Discount not found"));
            product.setDiscount(discount);
        }

        // Update other fields only if they are not null
        if (productUpdateDTO.getName() != null) {
            product.setName(productUpdateDTO.getName());
        }
        // Update brand if provided
        if (productUpdateDTO.getBrandId() != null) {
            Brand brand = brandRepository.findById(productUpdateDTO.getBrandId())
                    .orElseThrow(() -> new RuntimeException("Brand not found"));
            product.setBrand(brand);
        }
        if (productUpdateDTO.getPrice() != null) {
            product.setPrice(productUpdateDTO.getPrice());
        }
        if (productUpdateDTO.getDescription() != null) {
            product.setDescription(productUpdateDTO.getDescription());
        }
        if (productUpdateDTO.getWarranty() != null) {
            product.setWarranty(productUpdateDTO.getWarranty());
        }
        if (productUpdateDTO.getWeight() != null) {
            product.setWeight(productUpdateDTO.getWeight());
        }
        if (productUpdateDTO.getDimensions() != null) {
            product.setDimensions(productUpdateDTO.getDimensions());
        }
        if (productUpdateDTO.getStatus() != null) {
            product.setStatus(productUpdateDTO.getStatus());
        }
        if (productUpdateDTO.getStock() != null) {
            product.setStock(productUpdateDTO.getStock());
        }

        product.setUpdateBy("admin"); // This should be the current user

        Product updatedProduct = productRepository.save(product);

        // Update product detail if any technical specifications are provided
        if (productUpdateDTO.getProcessor() != null || productUpdateDTO.getRam() != null ||
                productUpdateDTO.getStorage() != null || productUpdateDTO.getDisplay() != null ||
                productUpdateDTO.getGraphics() != null || productUpdateDTO.getBattery() != null ||
                productUpdateDTO.getCamera() != null || productUpdateDTO.getOperatingSystem() != null ||
                productUpdateDTO.getConnectivity() != null || productUpdateDTO.getOtherFeatures() != null) {

            Optional<ProductDetail> optionalProductDetail = productDetailRepository.findByProduct(product);
            ProductDetail productDetail = optionalProductDetail.orElseGet(() -> {
                ProductDetail newDetail = new ProductDetail();
                newDetail.setProduct(updatedProduct);
                return newDetail;
            });

            // Update technical specifications only if they are provided
            if (productUpdateDTO.getProcessor() != null) {
                productDetail.setProcessor(productUpdateDTO.getProcessor());
            }
            if (productUpdateDTO.getRam() != null) {
                productDetail.setRam(productUpdateDTO.getRam());
            }
            if (productUpdateDTO.getStorage() != null) {
                productDetail.setStorage(productUpdateDTO.getStorage());
            }
            if (productUpdateDTO.getDisplay() != null) {
                productDetail.setDisplay(productUpdateDTO.getDisplay());
            }
            if (productUpdateDTO.getGraphics() != null) {
                productDetail.setGraphics(productUpdateDTO.getGraphics());
            }
            if (productUpdateDTO.getBattery() != null) {
                productDetail.setBattery(productUpdateDTO.getBattery());
            }
            if (productUpdateDTO.getCamera() != null) {
                productDetail.setCamera(productUpdateDTO.getCamera());
            }
            if (productUpdateDTO.getOperatingSystem() != null) {
                productDetail.setOperatingSystem(productUpdateDTO.getOperatingSystem());
            }
            if (productUpdateDTO.getConnectivity() != null) {
                productDetail.setConnectivity(productUpdateDTO.getConnectivity());
            }
            if (productUpdateDTO.getOtherFeatures() != null) {
                productDetail.setOtherFeatures(productUpdateDTO.getOtherFeatures());
            }

            productDetailRepository.save(productDetail);
        }

        return mapProductToDTO(updatedProduct);
    }

    @Override
    public ProductDTO getProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return mapProductToDTO(product);
    }

    @Override
    public Page<ProductDTO> getAllProducts(ProductFilterRequest filter) {
        Pageable pageable = filter.getPageable();

        // Nếu không có filter type, xử lý như filter thông thường
        if (filter.getFilterType() == null) {
            return handleDefaultFilter(filter, pageable);
        }

        // Xử lý theo filter type
        return switch (filter.getFilterType()) {
            case TOP_SELLING -> handleTopSellingProducts(pageable);
            case NEW_ARRIVALS -> handleNewArrivals(pageable);
            case TOP_RATED -> handleTopRated(pageable);
            case DISCOUNTED -> handleDiscountedProducts(pageable);
            case ALL -> handleDefaultFilter(filter, pageable);
            default -> handleDefaultFilter(filter, pageable);
        };
    }



    private Page<ProductDTO> handleDefaultFilter(ProductFilterRequest filter, Pageable pageable) {
        Specification<Product> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            // Thêm điều kiện cơ bản cho sản phẩm active
            predicates.add(cb.isTrue(root.get("status")));

            // Tìm kiếm theo keyword
            if (StringUtils.hasText(filter.getKeyword())) {
                predicates.add(cb.like(cb.lower(root.get("name")),
                        "%" + filter.getKeyword().toLowerCase() + "%"));
            }

            // Lọc theo category
            if (filter.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("category").get("id"),
                        filter.getCategoryId()));
            }

            // Lọc theo brand
            if (StringUtils.hasText(filter.getBrand())) {
                predicates.add(cb.equal(root.get("brand").get("brandName"), filter.getBrand()));
            }

            // Lọc theo khoảng giá
            if (filter.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"),
                        filter.getMinPrice()));
            }
            if (filter.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"),
                        filter.getMaxPrice()));
            }

            // Lọc sản phẩm đang giảm giá
            if (Boolean.TRUE.equals(filter.getIsDiscount())) {
                predicates.add(cb.isNotNull(root.get("discount")));
            }

            // Lọc sản phẩm còn hàng
            if (Boolean.TRUE.equals(filter.getInStock())) {
                predicates.add(cb.greaterThan(root.get("stock"), 0));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        return productRepository.findAll(spec, pageable).map(this::mapProductToDTO);
    }

    private Page<ProductDTO> handleTopSellingProducts(Pageable pageable) {
        return productRepository.findTopSellingProducts(pageable)
                .map(this::mapProductToDTO);
    }

    private Page<ProductDTO> handleNewArrivals(Pageable pageable) {
        return productRepository.findByOrderByCreateAtDesc(pageable)
                .map(this::mapProductToDTO);
    }

    private Page<ProductDTO> handleTopRated(Pageable pageable) {
        return productRepository.findTopRatedProducts(pageable)
                .map(this::mapProductToDTO);
    }

    private Page<ProductDTO> handleDiscountedProducts(Pageable pageable) {
        return productRepository.findDiscountedProducts(pageable)
                .map(this::mapProductToDTO);
    }

    @Override
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::mapProductToDTO);
    }

    @Override
    public Page<ProductDTO> getAllActiveProducts(Pageable pageable) {
        return productRepository.findAllActiveProducts(pageable)
                .map(this::mapProductToDTO);
    }

    @Override
    public List<ProductDTO> getProductsByCategory(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        return productRepository.findByCategory(category).stream()
                .map(this::mapProductToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> searchProducts(String keyword) {
        return productRepository.findByNameContaining(keyword).stream()
                .map(this::mapProductToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getProductsByPriceRange(Integer minPrice, Integer maxPrice) {
        return productRepository.findByPriceRange(minPrice, maxPrice).stream()
                .map(this::mapProductToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getProductsByBrand(String brandName) {
        Brand brand = brandRepository.findByBrandName(brandName)
                .orElseThrow(() -> new RuntimeException("Brand not found: " + brandName));

        // Tìm sản phẩm dựa trên Brand entity
        return productRepository.findByBrandAndStatusTrue(brand).stream()
                .map(this::mapProductToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getTopSellingProducts() {
        return productRepository.findTopSellingProducts().stream()
                .map(this::mapProductToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getTopRatedProducts() {
        return productRepository.findTopRatedProducts().stream()
                .map(this::mapProductToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getRecommendedProducts(Integer userId) {
        // This would typically use a recommendation algorithm
        // For now, return top selling products as a placeholder
        return getTopSellingProducts();
    }


    @Override
    public List<ProductDTO> getNewArrivals() {
        // Get the 10 most recently added products
        return productRepository.findAll().stream()
                .sorted(Comparator.comparing(Product::getCreateAt).reversed())
                .limit(10)
                .map(this::mapProductToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getDiscountedProducts() {
        // Get products with discounts
        return productRepository.findAll().stream()
                .filter(p -> p.getDiscount() != null)
                .map(this::mapProductToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getLowStockProducts() {
        return productRepository.findLowStockProducts().stream()
                .map(this::mapProductToDTO)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public void updateProductStock(Integer id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        product.setStock(quantity);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public String uploadProductImage(Integer productId, MultipartFile file, boolean isPrimary) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        try {
            // Upload to Cloudinary
            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "folder", "products/" + productId,
                    "resource_type", "auto"
            );

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
            String imageUrl = uploadResult.get("secure_url").toString();

            // Save image info to database
            ProductImage productImage = new ProductImage();
            productImage.setProduct(product);
            productImage.setImageUrl(imageUrl);
            productImage.setIsPrimary(isPrimary);

           //set primary image
            if (isPrimary) {
                // Set all other images as non-primary
                List<ProductImage> existingImages = productImageRepository.findByProduct(product);
                existingImages.forEach(img -> img.setIsPrimary(false));
                productImageRepository.saveAll(existingImages);

                // Update product's main image
                product.setImage(imageUrl);
                productRepository.save(product);
            }

            productImageRepository.save(productImage);

            return imageUrl;

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteProductImage(Integer imageId) {
        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        Product product = productImage.getProduct();
        String imageUrl = productImage.getImageUrl();
        try {
            // Extract public_id from Cloudinary URL
            String[] urlParts = imageUrl.split("/");
            String publicId = String.join("/",
                            Arrays.copyOfRange(urlParts, urlParts.length - 2, urlParts.length))
                    .replaceFirst("[.][^.]+$", ""); // Remove file extension

            // Delete from Cloudinary
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

            // If this image URL is the same as product's main image, clear it
            if (imageUrl.equals(product.getImage())) {
                product.setImage(null);
                productRepository.save(product);
            }

            // Delete from database
            productImageRepository.delete(productImage);

        } catch (IOException e) {
            throw new RuntimeException("Failed to delete image: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updatePrimaryImage(Integer imageId) {
        ProductImage newPrimaryImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        Product product = newPrimaryImage.getProduct();
        // Set the selected image as primary
        newPrimaryImage.setIsPrimary(true);

        // Update product's main image
        product.setImage(newPrimaryImage.getImageUrl());
        productRepository.save(product);
    }

    @Override
    public ProductDetailDTO getProductDetail(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        ProductDetail productDetail = productDetailRepository.findByProduct(product)
                .orElseThrow(() -> new RuntimeException("Product detail not found"));
        
        return mapProductDetailToDTO(productDetail);
    }

    @Override
    @Transactional
    public ProductDetailDTO updateProductDetail(Integer productId, ProductDetailDTO productDetailDTO) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        ProductDetail productDetail = productDetailRepository.findByProduct(product)
                .orElse(new ProductDetail());
        
        productDetail.setProduct(product);
        productDetail.setProcessor(productDetailDTO.getProcessor());
        productDetail.setRam(productDetailDTO.getRam());
        productDetail.setStorage(productDetailDTO.getStorage());
        productDetail.setDisplay(productDetailDTO.getDisplay());
        productDetail.setGraphics(productDetailDTO.getGraphics());
        productDetail.setBattery(productDetailDTO.getBattery());
        productDetail.setCamera(productDetailDTO.getCamera());
        productDetail.setOperatingSystem(productDetailDTO.getOperatingSystem());
        productDetail.setConnectivity(productDetailDTO.getConnectivity());
        productDetail.setOtherFeatures(productDetailDTO.getOtherFeatures());
        
        ProductDetail savedProductDetail = productDetailRepository.save(productDetail);
        return mapProductDetailToDTO(savedProductDetail);
    }

    @Override
    public List<String> getAllBrands() {
        return brandRepository.findAllActiveBrands().stream()
                .map(Brand::getBrandName)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Product> getProductEntityById(Integer id) {
        return productRepository.findById(id);
    }

    @Override
    public List<ProductDTO> compareProducts(List<Integer> productIds) {
        if (productIds.size() < 2 || productIds.size() > 4) {
            throw new RuntimeException("You can compare between 2 and 4 products");
        }
        
        return productIds.stream()
                .map(id -> productRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Product not found: " + id)))
                .map(this::mapProductToDTO)
                .collect(Collectors.toList());
    }
    
    // Helper methods
    private ProductDTO mapProductToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
        dto.setCategoryName(product.getCategory() != null ? product.getCategory().getCategoryName() : null);
        dto.setDiscountId(product.getDiscount() != null ? product.getDiscount().getId() : null);
        dto.setDiscountName(product.getDiscount() != null ? product.getDiscount().getDiscountName() : null);
        dto.setBrandId(product.getBrand() != null ? product.getBrand().getId() : null);
        dto.setBrandName(product.getBrand() != null ? product.getBrand().getBrandName() : null);
        dto.setName(product.getName());
        dto.setImage(product.getImage());
        dto.setPrice(product.getPrice());
        
        // Calculate discounted price if discount exists
        if (product.getDiscount() != null) {
            double discountValue = product.getDiscount().getValue();
            dto.setDiscountedPrice((int) (product.getPrice() * (1 - discountValue / 100)));
        } else {
            dto.setDiscountedPrice(product.getPrice());
        }
        
        dto.setDescription(product.getDescription());
        dto.setWarranty(product.getWarranty());
        dto.setWeight(product.getWeight());
        dto.setDimensions(product.getDimensions());
        dto.setCreateAt(product.getCreateAt());
        dto.setCreateBy(product.getCreateBy());
        dto.setStatus(product.getStatus());
        dto.setUpdateAt(product.getUpdateAt());
        dto.setUpdateBy(product.getUpdateBy());
        dto.setStock(product.getStock());
        
        // Get average rating
        Double avgRating = ratingRepository.getAverageRatingForProduct(product.getId());
        dto.setAverageRating(avgRating != null ? avgRating : 0.0);
        
        // Get review count
        Long reviewCount = ratingRepository.countRatingsByProduct(product.getId());
        dto.setReviewCount(reviewCount != null ? reviewCount : 0L);
        
        // Get all images
        List<ProductImageDTO> imageList = productImageRepository.findByProductOrderByDisplayOrder(product).stream()
                .map(image -> ProductImageDTO.builder()
                        .id(image.getId())
                        .imageUrl(image.getImageUrl())
                        .isPrimary(image.getIsPrimary())
                        .displayOrder(image.getDisplayOrder())
                        .build())
                .collect(Collectors.toList());
        dto.setProductImages(imageList);
        
        // Get product detail
        productDetailRepository.findByProduct(product).ifPresent(detail -> {
            dto.setProductDetail(mapProductDetailToDTO(detail));
        });
        
        return dto;
    }
    
    private ProductDetailDTO mapProductDetailToDTO(ProductDetail productDetail) {
        ProductDetailDTO dto = new ProductDetailDTO();
        dto.setId(productDetail.getId());
        dto.setProductId(productDetail.getProduct().getId());
        dto.setProcessor(productDetail.getProcessor());
        dto.setRam(productDetail.getRam());
        dto.setStorage(productDetail.getStorage());
        dto.setDisplay(productDetail.getDisplay());
        dto.setGraphics(productDetail.getGraphics());
        dto.setBattery(productDetail.getBattery());
        dto.setCamera(productDetail.getCamera());
        dto.setOperatingSystem(productDetail.getOperatingSystem());
        dto.setConnectivity(productDetail.getConnectivity());
        dto.setOtherFeatures(productDetail.getOtherFeatures());
        return dto;
    }
}
