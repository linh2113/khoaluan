package com.example.electronics_store.controller;

import com.example.electronics_store.dto.*;
import com.example.electronics_store.service.*;
import com.example.electronics_store.service.domain.DiscountEligibilityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final UserService userService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final OrderService orderService;
    private final DiscountService discountService;
    private final RatingService ratingService;
    private final StatisticsService statisticsService;
    private final BrandService brandService;
    private final ShippingMethodService shippingMethodService;
    private final PaymentMethodService paymentMethodService;
    private final DiscountEligibilityService discountEligibilityService;
    private final ProductSalesService productSalesService;
    private final FlashSaleService flashSaleService;

    @Autowired
    public AdminController(
            UserService userService,
            ProductService productService,
            CategoryService categoryService,
            OrderService orderService,
            DiscountService discountService,
            RatingService ratingService,
            StatisticsService statisticsService, BrandService brandService, ShippingMethodService shippingMethodService, PaymentMethodService paymentMethodService,DiscountEligibilityService discountEligibilityService, ProductSalesService productSalesService, FlashSaleService flashSaleService) {
        this.userService = userService;
        this.productService = productService;
        this.categoryService = categoryService;
        this.orderService = orderService;
        this.discountService = discountService;
        this.ratingService = ratingService;
        this.statisticsService = statisticsService;
        this.brandService = brandService;
        this.shippingMethodService = shippingMethodService;
        this.paymentMethodService = paymentMethodService;
        this.discountEligibilityService = discountEligibilityService;
        this.productSalesService = productSalesService;
        this.flashSaleService = flashSaleService;
    }

    // User Management - Unified API with filtering and pagination
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<?>> getUsers(
            @RequestParam(required = false) Boolean role,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<UserDTO> userPage = userService.getUsersWithFilters(role, search, pageable);
        return ResponseEntity.ok(ApiResponse.success(userPage));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<ApiResponse<?>> updateUser(
            @PathVariable Integer id,
            @Valid @RequestBody UserDTO userDTO) {
        
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật người dùng thành công", updatedUser));
    }

    // Product Management
    @PostMapping("/products")
    public ResponseEntity<ApiResponse<?>> createProduct(@Valid @RequestBody ProductCreateDTO productCreateDTO) {
        ProductDTO product = productService.createProduct(productCreateDTO);
        return ResponseEntity.ok(ApiResponse.success("Tạo thành công sản phẩm", product));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ApiResponse<?>> updateProduct(
            @PathVariable Integer id,
            @Valid @RequestBody ProductUpdateDTO productUpdateDTO) {
        
        ProductDTO product = productService.updateProduct(id, productUpdateDTO);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật sản phẩm thành công", product));
    }

    @PostMapping("/products/{id}/upload-image")
    public ResponseEntity<ApiResponse<?>> uploadProductImage(
            @PathVariable Integer id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "false") boolean isPrimary) {
        
        String imageUrl = productService.uploadProductImage(id, file, isPrimary);
        return ResponseEntity.ok(ApiResponse.success("Upload ảnh thành công", imageUrl));
    }

    @DeleteMapping("/products/images/{imageId}")
    public ResponseEntity<ApiResponse<?>> deleteProductImage(@PathVariable Integer imageId) {
        productService.deleteProductImage(imageId);
        return ResponseEntity.ok(ApiResponse.success("Xóa ảnh thành công"));
    }

    @PutMapping("/products/images/{imageId}/primary")
    public ResponseEntity<ApiResponse<?>> updatePrimaryImage(@PathVariable Integer imageId) {
        productService.updatePrimaryImage(imageId);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật ảnh chính thành công"));
    }

    @PutMapping("/products/{id}/stock")
    public ResponseEntity<ApiResponse<?>> updateProductStock(
            @PathVariable Integer id,
            @RequestParam Integer quantity) {
        
        productService.updateProductStock(id, quantity);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật tồn kho thành công"));
    }

    // Category Management
    @PostMapping(value = "/categories", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> createCategory(
            @RequestPart("category") @Valid CategoryDTO categoryDTO,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        
        categoryDTO.setImageFile(image);
        CategoryDTO category = categoryService.createCategory(categoryDTO);
        return ResponseEntity.ok(ApiResponse.success("Tạo danh mục thành công", category));
    }

    @PutMapping(value = "/categories/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> updateCategory(
            @PathVariable Integer id,
            @RequestPart("category") @Valid CategoryUpdateDTO categoryUpdateDTO,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        
        // Gán file vào DTO
        categoryUpdateDTO.setImageFile(image);

        // Convert CategoryUpdateDTO to CategoryDTO
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryName(categoryUpdateDTO.getCategoryName());
        categoryDTO.setStatus(categoryUpdateDTO.getStatus());
        categoryDTO.setImageUrl(categoryUpdateDTO.getImageUrl());
        categoryDTO.setImageFile(categoryUpdateDTO.getImageFile());

        CategoryDTO category = categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật danh mục thành công", category));
    }

    // Order Management
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<?>> getAllOrders(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrderDTO> orderPage = orderService.getOrdersWithSearch(search, pageable);

        return ResponseEntity.ok(ApiResponse.success(orderPage));
    }

    @GetMapping("/orders/status/{status}")
    public ResponseEntity<ApiResponse<?>> getOrdersByStatus(@PathVariable Integer status) {
        List<OrderDTO> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/orders/payment/{paymentStatus}")
    public ResponseEntity<ApiResponse<?>> getOrdersByPaymentStatus(@PathVariable String paymentStatus) {
        List<OrderDTO> orders = orderService.getOrdersByPaymentStatus(paymentStatus);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @PostMapping(value = "/discounts/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> updateDiscount(
            @PathVariable Integer id,
            @RequestPart("discount") @Valid DiscountUpdateDTO discountUpdateDTO,
            @RequestParam(value = "banner", required = false) MultipartFile banner) {
        
        discountUpdateDTO.setBannerFile(banner);
        DiscountDTO discount = discountService.updateDiscount(id, discountUpdateDTO);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật mã giảm giá thành công", discount));
    }

    @GetMapping("/discounts")
    public ResponseEntity<ApiResponse<?>> getAllDiscounts(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<DiscountDTO> discounts = discountService.getAllDiscounts(search, pageable);

        return ResponseEntity.ok(ApiResponse.success(discounts));
    }

    @GetMapping("/ratings")
    public ResponseEntity<ApiResponse<?>> getAllRatings(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<RatingDTO> ratingPage = ratingService.getRatingsWithSearch(search, pageable);

        return ResponseEntity.ok(ApiResponse.success(ratingPage));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<?>> getDashboardStatistics() {
        return ResponseEntity.ok(ApiResponse.success(statisticsService.getDashboardStatistics()));
    }

    // Get all categories
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<?>> getAllCategories(
        @RequestParam(required = false) String search,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CategoryDTO> categoryPage = categoryService.getCategoriesWithSearch(search, pageable);

        return ResponseEntity.ok(ApiResponse.success(categoryPage));
    }

    // Get all products
    @GetMapping("/products")
    public ResponseEntity<ApiResponse<?>> getAllProducts(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductDTO> products;

        if (search != null && !search.trim().isEmpty()) {
            products = productService.getProductsWithSearch(search, pageable);
        } else {
            products = productService.getAllProducts(pageable);
        }

        return ResponseEntity.ok(ApiResponse.success(products));
    }

    // Get low stock products
    @GetMapping("/products/low-stock")
    public ResponseEntity<ApiResponse<?>> getLowStockProducts() {
        List<ProductDTO> products = productService.getLowStockProducts();
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    // Update order status
    @PutMapping("/orders/{id}/status")
    public ResponseEntity<ApiResponse<?>> updateOrderStatus(
            @PathVariable Integer id,
            @RequestParam Integer status) {
        
        OrderDTO order = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái đơn hàng thành công", order));
    }

    // Update payment status
    @PutMapping("/orders/{id}/payment")
    public ResponseEntity<ApiResponse<?>> updatePaymentStatus(
            @PathVariable Integer id,
            @RequestParam String paymentStatus) {
        
        OrderDTO order = orderService.updatePaymentStatus(id, paymentStatus);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thanh toán thành công", order));
    }

    @GetMapping("/statistics/sales")
    public ResponseEntity<ApiResponse<?>> getSalesStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        // Convert string dates to LocalDateTime if provided
        LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : LocalDateTime.now().minusMonths(1);
        LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : LocalDateTime.now();

        Map<String, Object> statistics = statisticsService.getSalesStatistics(start, end);
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }

    @GetMapping("/brands")
    public ResponseEntity<ApiResponse<?>> getAllBrands(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<BrandDTO> brandPage = brandService.getBrandsWithSearch(search, pageable);

        return ResponseEntity.ok(ApiResponse.success(brandPage));
    }

    @PostMapping(value = "/brands", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> createBrand(
            @RequestPart("brand") @Valid BrandDTO brandDTO,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        
        brandDTO.setImageFile(image);
        BrandDTO brand = brandService.createBrand(brandDTO);
        return ResponseEntity.ok(ApiResponse.success("Tạo thương hiệu thành công", brand));
    }

    @PutMapping(value = "/brands/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> updateBrand(
            @PathVariable Integer id,
            @RequestPart("brand") @Valid BrandDTO brandDTO,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        
        // Gán file vào DTO
        brandDTO.setImageFile(image);

        BrandDTO brand = brandService.updateBrand(id, brandDTO);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thương hiệu thành công", brand));
    }

    @GetMapping("/shipping-methods")
    public ResponseEntity<ApiResponse<?>> getAllShippingMethods() {
        List<ShippingMethodDTO> shippingMethods = shippingMethodService.getAllShippingMethods();
        return ResponseEntity.ok(ApiResponse.success(shippingMethods));
    }

    @GetMapping("/shipping-methods/{id}")
    public ResponseEntity<ApiResponse<?>> getShippingMethodById(@PathVariable Integer id) {
        ShippingMethodDTO shippingMethod = shippingMethodService.getShippingMethodById(id);
        return ResponseEntity.ok(ApiResponse.success(shippingMethod));
    }

    @PostMapping("/shipping-methods")
    public ResponseEntity<ApiResponse<?>> createShippingMethod(@Valid @RequestBody ShippingMethodDTO shippingMethodDTO) {
        ShippingMethodDTO createdShippingMethod = shippingMethodService.createShippingMethod(shippingMethodDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo phương thức vận chuyển thành công", createdShippingMethod));
    }

    @PutMapping("/shipping-methods/{id}")
    public ResponseEntity<ApiResponse<?>> updateShippingMethod(
            @PathVariable Integer id,
            @Valid @RequestBody ShippingMethodDTO shippingMethodDTO) {
        
        ShippingMethodDTO updatedShippingMethod = shippingMethodService.updateShippingMethod(id, shippingMethodDTO);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật phương thức vận chuyển thành công", updatedShippingMethod));
    }

    @PutMapping("/shipping-methods/{id}/toggle-status")
    public ResponseEntity<ApiResponse<?>> toggleShippingMethodStatus(@PathVariable Integer id) {
        shippingMethodService.toggleShippingMethodStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật phương thức vận chuyển thành công"));
    }

    // Payment Method Management
    @GetMapping("/payment-methods")
    public ResponseEntity<ApiResponse<?>> getAllPaymentMethods() {
        List<PaymentMethodDTO> paymentMethods = paymentMethodService.getAllPaymentMethods();
        return ResponseEntity.ok(ApiResponse.success(paymentMethods));
    }

    @GetMapping("/payment-methods/{id}")
    public ResponseEntity<ApiResponse<?>> getPaymentMethodById(@PathVariable Integer id) {
        PaymentMethodDTO paymentMethod = paymentMethodService.getPaymentMethodById(id);
        return ResponseEntity.ok(ApiResponse.success(paymentMethod));
    }

    @PostMapping("/payment-methods")
    public ResponseEntity<ApiResponse<?>> createPaymentMethod(@Valid @RequestBody PaymentMethodDTO paymentMethodDTO) {
        PaymentMethodDTO createdPaymentMethod = paymentMethodService.createPaymentMethod(paymentMethodDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo phương thức thanh toán thành công", createdPaymentMethod));
    }

    @PutMapping("/payment-methods/{id}")
    public ResponseEntity<ApiResponse<?>> updatePaymentMethod(
            @PathVariable Integer id,
            @Valid @RequestBody PaymentMethodDTO paymentMethodDTO) {
        
        PaymentMethodDTO updatedPaymentMethod = paymentMethodService.updatePaymentMethod(id, paymentMethodDTO);
        return ResponseEntity.ok(ApiResponse.success("Thay đổi phương thức thanh toán thành công", updatedPaymentMethod));
    }

    @PutMapping("/payment-methods/{id}/toggle-status")
    public ResponseEntity<ApiResponse<?>> togglePaymentMethodStatus(@PathVariable Integer id) {
        paymentMethodService.togglePaymentMethodStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Thay đổi phương thức thanh toán thành công"));
    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<?>> createUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        UserDTO createdUser = userService.createUserByAdmin(registrationDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo thành công người dùng", createdUser));
    }

    @PostMapping(value = "/discounts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> createDiscount(
            @RequestPart("discount") @Valid DiscountDTO discountDTO,
            @RequestParam(value = "banner", required = false) MultipartFile banner) {
        
        // Gán file vào DTO
        discountDTO.setBannerFile(banner);

        DiscountDTO discount = discountService.createDiscount(discountDTO);
        return ResponseEntity.ok(ApiResponse.success("Tạo thành công mã giảm giá", discount));
    }

    // Gắn discount với nhiều sản phẩm
    @PostMapping("/discounts/{discountId}/assign-products")
    public ResponseEntity<ApiResponse<?>> assignDiscountToProducts(
            @PathVariable Integer discountId,
            @RequestBody BatchAssignRequest request) {
        
        Integer successCount = discountService.assignDiscountToProducts(
                discountId,
                request.getProductIds(),
                request.getDiscountedPrices()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("successCount", successCount);
        response.put("totalCount", request.getProductIds().size());

        return ResponseEntity.ok(ApiResponse.success(
                "Gắn thành công" + successCount + " trên" + request.getProductIds().size() + " số sản phẩm",
                response
        ));
    }

    // gắn discount với nhiều danh mục
    @PostMapping("/discounts/{discountId}/assign-categories")
    public ResponseEntity<ApiResponse<?>> assignDiscountToCategories(
            @PathVariable Integer discountId,
            @RequestBody Map<String, List<Integer>> request) {
        
        List<Integer> categoryIds = request.get("categoryIds");
        if (categoryIds == null) {
            throw new IllegalArgumentException("Yêu cầu categoryid");
        }

        Integer successCount = discountService.assignDiscountToCategories(discountId, categoryIds);

        Map<String, Object> response = new HashMap<>();
        response.put("successCount", successCount);
        response.put("totalCount", categoryIds.size());

        return ResponseEntity.ok(ApiResponse.success(
                "Gắn thành công " + successCount + " trên " + categoryIds.size() + " danh mục",
                response
        ));
    }

    @GetMapping("/discounts/active")
    public ResponseEntity<ApiResponse<?>> getActiveDiscounts() {
        List<DiscountDTO> discounts = discountService.getAllActiveDiscounts();
        return ResponseEntity.ok(ApiResponse.success(discounts));
    }

    @GetMapping("/discounts/expired")
    public ResponseEntity<ApiResponse<?>> getExpiredDiscounts() {
        List<DiscountDTO> discounts = discountService.getAllExpiredDiscounts();
        return ResponseEntity.ok(ApiResponse.success(discounts));
    }

    @GetMapping("/discounts/upcoming")
    public ResponseEntity<ApiResponse<?>> getUpcomingDiscounts() {
        List<DiscountDTO> discounts = discountService.getAllUpcomingDiscounts();
        return ResponseEntity.ok(ApiResponse.success(discounts));
    }

    @GetMapping("/discounts/product-discounts")
    public ResponseEntity<ApiResponse<?>> getProductDiscounts() {
        List<DiscountDTO> discounts = discountService.getAllProductDiscounts();
        return ResponseEntity.ok(ApiResponse.success(discounts));
    }

    @GetMapping("/discounts/category-discounts")
    public ResponseEntity<ApiResponse<?>> getCategoryDiscounts() {
            List<DiscountDTO> discounts = discountService.getAllCategoryDiscounts();
            return ResponseEntity.ok(ApiResponse.success(discounts));
    }

    @DeleteMapping("/discounts/{discountId}/products")
        public ResponseEntity<ApiResponse<?>> removeProductsFromDiscount(
            @PathVariable Integer discountId,
            @RequestBody Map<String, List<Integer>> request) {
        try {
            List<Integer> productIds = request.get("productIds");
            Integer count = discountService.removeProductsFromDiscount(discountId, productIds);
            return ResponseEntity.ok(ApiResponse.success("Xóa thành công " + count + " sản phẩm khỏi discount"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // API cho Product Discount - Cập nhật giá nhiều sản phẩm
    @PutMapping("/discounts/{discountId}/products/prices")
    public ResponseEntity<ApiResponse<?>> updateProductDiscountPrices(
        @PathVariable Integer discountId,
        @RequestBody Map<String, Map<Integer, Integer>> request) {
        
        Map<Integer, Integer> productPrices = request.get("productPrices");
        Integer count = discountService.updateProductDiscountPrices(discountId, productPrices);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thành công" + count + " Giá trị sản phẩm"));
    }

    @DeleteMapping("/discounts/{discountId}/categories")
    public ResponseEntity<ApiResponse<?>> removeCategoriesFromDiscount(
        @PathVariable Integer discountId,
        @RequestBody Map<String, List<Integer>> request) {
        List<Integer> categoryIds = request.get("categoryIds");
        Integer count = discountService.removeCategoriesFromDiscount(discountId, categoryIds);
        return ResponseEntity.ok(ApiResponse.success("Xóa thành công" + count + " danh mục khỏi discount"));
    }

    @GetMapping("/products/eligible-for-discount")
    public ResponseEntity<ApiResponse<?>> getProductsEligibleForDiscount(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() :
                    Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<ProductDTO> products = discountEligibilityService.getEligibleProducts(search, pageable);

            return ResponseEntity.ok(ApiResponse.success(products));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/products/best-selling")
    public ResponseEntity<ApiResponse<?>> getBestSellingProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "soldQuantity") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductDTO> products = productSalesService.getBestSellingProducts(pageable);

        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @PostMapping("/products/recalculate-sold-quantities")
    public ResponseEntity<ApiResponse<?>> recalculateAllSoldQuantities() {
        productSalesService.recalculateAllSoldQuantities();
        return ResponseEntity.ok(ApiResponse.success("Successfully recalculated all sold quantities"));
    }

    @PostMapping("/products/{id}/recalculate-sold-quantity")
    public ResponseEntity<ApiResponse<?>> recalculateSoldQuantity(@PathVariable Integer id) {
        productSalesService.recalculateSoldQuantity(id);
        return ResponseEntity.ok(ApiResponse.success("Successfully recalculated sold quantity for product " + id));
    }


    @PostMapping("/flash-sales")
    public ResponseEntity<ApiResponse<?>> createFlashSale(@Valid @RequestBody CreateFlashSaleDTO createFlashSaleDTO) {
        try {
            FlashSaleDTO flashSaleDTO = FlashSaleDTO.builder()
                .name(createFlashSaleDTO.getName())
                .description(createFlashSaleDTO.getDescription())
                .startTime(createFlashSaleDTO.getStartTime())
                .endTime(createFlashSaleDTO.getEndTime())
                .build();
            FlashSaleDTO createdFlashSale = flashSaleService.createFlashSale(flashSaleDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo thành công flashsale", createdFlashSale));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
    }}

    /**
     * Cập nhật flash sale (Admin only)
     */
    @PutMapping("/flash-sale/{id}")
    public ResponseEntity<ApiResponse<?>> updateFlashSale(@PathVariable Integer id,
                                                        @Valid @RequestBody FlashSaleDTO flashSaleDTO) {
        
        FlashSaleDTO updatedFlashSale = flashSaleService.updateFlashSale(id, flashSaleDTO);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật FlashSale thành công", updatedFlashSale));
    }

    /**
     * Xóa flash sale (Admin only)
     */
    @DeleteMapping("/flash-sale/{id}")
    public ResponseEntity<ApiResponse<?>> deleteFlashSale(@PathVariable Integer id) {
        flashSaleService.deleteFlashSale(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa FlashSale thành công", null));
    }

    @GetMapping("/flash-sales")
    public ResponseEntity<ApiResponse<?>> getAllFlashSales(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<FlashSaleDTO> flashSalePage = flashSaleService.getFlashSalesWithSearch(search, pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Lấy flassale thành công", flashSalePage));
    }

    /**
     * Thêm sản phẩm vào flash sale (Admin only)
     */
    @PostMapping("/flash-sales/{flashSaleId}/products")
    public ResponseEntity<ApiResponse<?>> addProductToFlashSale(@PathVariable Integer flashSaleId,
                                                               @Valid @RequestBody FlashSaleItemDTO flashSaleItemDTO) {
        try {
            FlashSaleItemDTO addedItem = flashSaleService.addProductToFlashSale(flashSaleId, flashSaleItemDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Product added to flash sale successfully", addedItem));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Cập nhật sản phẩm trong flash sale (Admin only)
     */
    @PutMapping("/flash-sales/items/{flashSaleItemId}")
    public ResponseEntity<ApiResponse<?>> updateFlashSaleItem(@PathVariable Integer flashSaleItemId,
                                                            @Valid @RequestBody FlashSaleItemDTO flashSaleItemDTO) {
        
        FlashSaleItemDTO updatedItem = flashSaleService.updateFlashSaleItem(flashSaleItemId, flashSaleItemDTO);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thành công FlashSale", updatedItem));
    }

    /**
     * Xóa sản phẩm khỏi flash sale (Admin only)
     */
    @DeleteMapping("/flash-sales/items/{flashSaleItemId}")
    public ResponseEntity<ApiResponse<?>> removeProductFromFlashSale(@PathVariable Integer flashSaleItemId) {
        try {
            flashSaleService.removeProductFromFlashSale(flashSaleItemId);
            return ResponseEntity.ok(ApiResponse.success("Product removed from flash sale successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/flash-sales/{flashSaleId}/items")
    public ResponseEntity<ApiResponse<?>> getFlashSaleItems(@PathVariable Integer flashSaleId) {
        try {
            List<FlashSaleItemDTO> items = flashSaleService.getFlashSaleItems(flashSaleId);
            return ResponseEntity.ok(ApiResponse.success("Flash sale items retrieved successfully", items));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Tìm kiếm flash sale theo tên (Admin only)
     */
    @GetMapping("/flash-sales/search")
    public ResponseEntity<ApiResponse<?>> searchFlashSales(@RequestParam String name) {
        List<FlashSaleDTO> flashSales = flashSaleService.searchFlashSalesByName(name);
        return ResponseEntity.ok(ApiResponse.success("Tìm thấy flashsale", flashSales));
    }

    /**
     * Lấy thống kê số lượng flash sale đang hoạt động (Admin only)
     */
    @GetMapping("/flash-sales/stats/active-count")
    public ResponseEntity<ApiResponse<?>> getActiveFlashSalesCount() {
        Long count = flashSaleService.countActiveFlashSales();
        return ResponseEntity.ok(ApiResponse.success("Số lượng flashSale đang hoạt đông: ", count));
    }

    @GetMapping("/revenue-by-interval")
    public ResponseEntity<ApiResponse<?>> getRevenueByTimeInterval(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "day") String interval) {
        try {
            // Chuyển đổi LocalDate sang LocalDateTime
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

            Map<String, Object> statistics = statisticsService.getRevenueByTimeInterval(startDateTime, endDateTime, interval);
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/revenue-by-category-pie")
    public ResponseEntity<ApiResponse<?>> getRevenueByCategoryPieChart(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        Map<String, Object> statistics = statisticsService.getRevenueByCategoryPieChart(startDateTime, endDateTime);
        return ResponseEntity.ok(ApiResponse.success(statistics));
            }

    @GetMapping("/{discountId}/products")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getProductsByDiscountId(@PathVariable Integer discountId) {
        List<ProductDTO> products = discountService.getProductsByDiscountId(discountId);
        return ResponseEntity.ok(ApiResponse.success("Lấy sản phẩm thuộc discount thành công", products));
    }
}
