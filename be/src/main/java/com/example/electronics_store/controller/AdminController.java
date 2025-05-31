package com.example.electronics_store.controller;

import com.example.electronics_store.dto.*;
import com.example.electronics_store.model.Discount;
import com.example.electronics_store.service.*;
import com.example.electronics_store.service.domain.DiscountEligibilityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @Autowired
    public AdminController(
            UserService userService,
            ProductService productService,
            CategoryService categoryService,
            OrderService orderService,
            DiscountService discountService,
            RatingService ratingService,
            StatisticsService statisticsService, BrandService brandService, ShippingMethodService shippingMethodService, PaymentMethodService paymentMethodService,DiscountEligibilityService discountEligibilityService) {
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
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() :
                    Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<UserDTO> userPage = userService.getUsersWithFilters(role, search, pageable);
            return ResponseEntity.ok(ApiResponse.success(userPage));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<ApiResponse<?>> updateUser(
            @PathVariable Integer id,
            @Valid @RequestBody UserDTO userDTO) {
        try {
            UserDTO updatedUser = userService.updateUser(id, userDTO);
            return ResponseEntity.ok(ApiResponse.success("User updated successfully", updatedUser));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }


    // Product Management
    @PostMapping("/products")
    public ResponseEntity<ApiResponse<?>> createProduct(@Valid @RequestBody ProductCreateDTO productCreateDTO) {
        try {
            ProductDTO product = productService.createProduct(productCreateDTO);
            return ResponseEntity.ok(ApiResponse.success("Product created successfully", product));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ApiResponse<?>> updateProduct(
            @PathVariable Integer id,
            @Valid @RequestBody ProductUpdateDTO productUpdateDTO) {
        try {
            ProductDTO product = productService.updateProduct(id, productUpdateDTO);
            return ResponseEntity.ok(ApiResponse.success("Product updated successfully", product));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }


    @PostMapping("/products/{id}/upload-image")
    public ResponseEntity<ApiResponse<?>> uploadProductImage(
            @PathVariable Integer id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "false") boolean isPrimary) {
        try {
            String imageUrl = productService.uploadProductImage(id, file, isPrimary);
            return ResponseEntity.ok(ApiResponse.success("Image uploaded successfully", imageUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/products/images/{imageId}")
    public ResponseEntity<ApiResponse<?>> deleteProductImage(@PathVariable Integer imageId) {
        try {
            productService.deleteProductImage(imageId);
            return ResponseEntity.ok(ApiResponse.success("Image deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/products/images/{imageId}/primary")
    public ResponseEntity<ApiResponse<?>> updatePrimaryImage(@PathVariable Integer imageId) {
        try {
            productService.updatePrimaryImage(imageId);
            return ResponseEntity.ok(ApiResponse.success("Primary image updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/products/{id}/stock")
    public ResponseEntity<ApiResponse<?>> updateProductStock(
            @PathVariable Integer id,
            @RequestParam Integer quantity) {
        try {
            productService.updateProductStock(id, quantity);
            return ResponseEntity.ok(ApiResponse.success("Product stock updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Category Management
    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<?>> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        try {
            CategoryDTO category = categoryService.createCategory(categoryDTO);
            return ResponseEntity.ok(ApiResponse.success("Category created successfully", category));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<?>> updateCategory(
            @PathVariable Integer id,
            @RequestBody CategoryUpdateDTO categoryUpdateDTO) {
        try {
            // Convert CategoryUpdateDTO to CategoryDTO
            CategoryDTO categoryDTO = new CategoryDTO();
            categoryDTO.setCategoryName(categoryUpdateDTO.getCategoryName());
            categoryDTO.setStatus(categoryUpdateDTO.getStatus());

            CategoryDTO category = categoryService.updateCategory(id, categoryDTO);
            return ResponseEntity.ok(ApiResponse.success("Category updated successfully", category));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Order Management
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<?>> getAllOrders(
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
            Page<OrderDTO> orderPage = orderService.getOrdersWithSearch(search, pageable);

            return ResponseEntity.ok(ApiResponse.success(orderPage));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    // Bỏ (lấy getOrder với search)
    @GetMapping("/orders/status/{status}")
    public ResponseEntity<ApiResponse<?>> getOrdersByStatus(@PathVariable Integer status) {
        try {
            List<OrderDTO> orders = orderService.getOrdersByStatus(status);
            return ResponseEntity.ok(ApiResponse.success(orders));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    // Bỏ (lấy getOrder với search)
    @GetMapping("/orders/payment/{paymentStatus}")
    public ResponseEntity<ApiResponse<?>> getOrdersByPaymentStatus(@PathVariable String paymentStatus) {
        try {
            List<OrderDTO> orders = orderService.getOrdersByPaymentStatus(paymentStatus);
            return ResponseEntity.ok(ApiResponse.success(orders));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }


    @PutMapping("/discounts/{id}")
    public ResponseEntity<ApiResponse<?>> updateDiscount(
            @PathVariable Integer id,
            @Valid @RequestBody DiscountUpdateDTO discountUpdateDTO) {
        try {
            DiscountDTO discount = discountService.updateDiscount(id, discountUpdateDTO);
            return ResponseEntity.ok(ApiResponse.success("Discount updated successfully", discount));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }



    @GetMapping("/discounts")
    public ResponseEntity<ApiResponse<?>> getAllDiscounts(
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
            Page<DiscountDTO> discounts = discountService.getAllDiscounts(search, pageable);

            return ResponseEntity.ok(ApiResponse.success(discounts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/ratings")
    public ResponseEntity<ApiResponse<?>> getAllRatings(
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
            Page<RatingDTO> ratingPage = ratingService.getRatingsWithSearch(search, pageable);

            return ResponseEntity.ok(ApiResponse.success(ratingPage));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }


    // not done
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<?>> getDashboardStatistics() {
        try {
            return ResponseEntity.ok(ApiResponse.success(statisticsService.getDashboardStatistics()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Get all categories
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<?>> getAllCategories(
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
                Page<CategoryDTO> categoryPage = categoryService.getCategoriesWithSearch(search, pageable);

                return ResponseEntity.ok(ApiResponse.success(categoryPage));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error(e.getMessage()));
            }
    }

   // Get all products
    @GetMapping("/products")
    public ResponseEntity<ApiResponse<?>> getAllProducts(
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
            Page<ProductDTO> products;
            
            if (search != null && !search.trim().isEmpty()) {
                products = productService.getProductsWithSearch(search, pageable);
            } else {
                products = productService.getAllProducts(pageable);
            }

            return ResponseEntity.ok(ApiResponse.success(products));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Get low stock products
    @GetMapping("/products/low-stock")
    public ResponseEntity<ApiResponse<?>> getLowStockProducts() {
        try {
            List<ProductDTO> products = productService.getLowStockProducts();
            return ResponseEntity.ok(ApiResponse.success(products));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Update order status
    @PutMapping("/orders/{id}/status")
    public ResponseEntity<ApiResponse<?>> updateOrderStatus(
            @PathVariable Integer id,
            @RequestParam Integer status) {
        try {
            OrderDTO order = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("Order status updated successfully", order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Update payment status
    @PutMapping("/orders/{id}/payment")
    public ResponseEntity<ApiResponse<?>> updatePaymentStatus(
            @PathVariable Integer id,
            @RequestParam String paymentStatus) {
        try {
            OrderDTO order = orderService.updatePaymentStatus(id, paymentStatus);
            return ResponseEntity.ok(ApiResponse.success("Payment status updated successfully", order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // not done
    @GetMapping("/statistics/sales")
    public ResponseEntity<ApiResponse<?>> getSalesStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            // Convert string dates to LocalDateTime if provided
            LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : LocalDateTime.now().minusMonths(1);
            LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : LocalDateTime.now();

            Map<String, Object> statistics = statisticsService.getSalesStatistics(start, end);
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    @GetMapping("/brands")
    public ResponseEntity<ApiResponse<?>> getAllBrands(
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
            Page<BrandDTO> brandPage = brandService.getBrandsWithSearch(search, pageable);

            return ResponseEntity.ok(ApiResponse.success(brandPage));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    @PostMapping("/brands")
    public ResponseEntity<ApiResponse<?>> createBrand(@Valid @RequestBody BrandDTO brandDTO) {
        try {
            BrandDTO createdBrand = brandService.createBrand(brandDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Brand created successfully", createdBrand));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    @PutMapping("/brands/{id}")
    public ResponseEntity<ApiResponse<?>> updateBrand(
            @PathVariable Integer id,
            @Valid @RequestBody BrandDTO brandDTO) {
        try {
            BrandDTO updatedBrand = brandService.updateBrand(id, brandDTO);
            return ResponseEntity.ok(ApiResponse.success("Brand updated successfully", updatedBrand));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    @GetMapping("/shipping-methods")
    public ResponseEntity<ApiResponse<?>> getAllShippingMethods() {
        try {
            List<ShippingMethodDTO> shippingMethods = shippingMethodService.getAllShippingMethods();
            return ResponseEntity.ok(ApiResponse.success(shippingMethods));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/shipping-methods/{id}")
    public ResponseEntity<ApiResponse<?>> getShippingMethodById(@PathVariable Integer id) {
        try {
            ShippingMethodDTO shippingMethod = shippingMethodService.getShippingMethodById(id);
            return ResponseEntity.ok(ApiResponse.success(shippingMethod));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/shipping-methods")
    public ResponseEntity<ApiResponse<?>> createShippingMethod(@Valid @RequestBody ShippingMethodDTO shippingMethodDTO) {
        try {
            ShippingMethodDTO createdShippingMethod = shippingMethodService.createShippingMethod(shippingMethodDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Shipping method created successfully", createdShippingMethod));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/shipping-methods/{id}")
    public ResponseEntity<ApiResponse<?>> updateShippingMethod(
            @PathVariable Integer id,
            @Valid @RequestBody ShippingMethodDTO shippingMethodDTO) {
        try {
            ShippingMethodDTO updatedShippingMethod = shippingMethodService.updateShippingMethod(id, shippingMethodDTO);
            return ResponseEntity.ok(ApiResponse.success("Shipping method updated successfully", updatedShippingMethod));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    @PutMapping("/shipping-methods/{id}/toggle-status")
    public ResponseEntity<ApiResponse<?>> toggleShippingMethodStatus(@PathVariable Integer id) {
        try {
            shippingMethodService.toggleShippingMethodStatus(id);
            return ResponseEntity.ok(ApiResponse.success("Shipping method status toggled successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    // Payment Method Management
    @GetMapping("/payment-methods")
    public ResponseEntity<ApiResponse<?>> getAllPaymentMethods() {
        try {
            List<PaymentMethodDTO> paymentMethods = paymentMethodService.getAllPaymentMethods();
            return ResponseEntity.ok(ApiResponse.success(paymentMethods));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/payment-methods/{id}")
    public ResponseEntity<ApiResponse<?>> getPaymentMethodById(@PathVariable Integer id) {
        try {
            PaymentMethodDTO paymentMethod = paymentMethodService.getPaymentMethodById(id);
            return ResponseEntity.ok(ApiResponse.success(paymentMethod));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/payment-methods")
    public ResponseEntity<ApiResponse<?>> createPaymentMethod(@Valid @RequestBody PaymentMethodDTO paymentMethodDTO) {
        try {
            PaymentMethodDTO createdPaymentMethod = paymentMethodService.createPaymentMethod(paymentMethodDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Payment method created successfully", createdPaymentMethod));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/payment-methods/{id}")
    public ResponseEntity<ApiResponse<?>> updatePaymentMethod(
            @PathVariable Integer id,
            @Valid @RequestBody PaymentMethodDTO paymentMethodDTO) {
        try {
            PaymentMethodDTO updatedPaymentMethod = paymentMethodService.updatePaymentMethod(id, paymentMethodDTO);
            return ResponseEntity.ok(ApiResponse.success("Payment method updated successfully", updatedPaymentMethod));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    @PutMapping("/payment-methods/{id}/toggle-status")
    public ResponseEntity<ApiResponse<?>> togglePaymentMethodStatus(@PathVariable Integer id) {
        try {
            paymentMethodService.togglePaymentMethodStatus(id);
            return ResponseEntity.ok(ApiResponse.success("Payment method status toggled successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<?>> createUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        try {
            UserDTO createdUser = userService.registerUser(registrationDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("User created successfully", createdUser));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    @PostMapping("/discounts")
    public ResponseEntity<ApiResponse<?>> createDiscount(@Valid @RequestBody DiscountDTO discountDTO) {
        try {
            DiscountDTO discount = discountService.createDiscount(discountDTO);
            return ResponseEntity.ok(ApiResponse.success("Discount created successfully", discount));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }



    // Gắn discount với nhiều sản phẩm
    @PostMapping("/discounts/{discountId}/assign-products")
    public ResponseEntity<ApiResponse<?>> assignDiscountToProducts(
            @PathVariable Integer discountId,
            @RequestBody BatchAssignRequest request) {
        try {
            Integer successCount = discountService.assignDiscountToProducts(
                    discountId,
                    request.getProductIds(),
                    request.getDiscountedPrices()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("successCount", successCount);
            response.put("totalCount", request.getProductIds().size());

            return ResponseEntity.ok(ApiResponse.success(
                    "Successfully assigned " + successCount + " out of " + request.getProductIds().size() + " products",
                    response
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // gắn discount với nhiều danh mục
    @PostMapping("/discounts/{discountId}/assign-categories")
    public ResponseEntity<ApiResponse<?>> assignDiscountToCategories(
            @PathVariable Integer discountId,
            @RequestBody Map<String, List<Integer>> request) {
        try {
            List<Integer> categoryIds = request.get("categoryIds");
            if (categoryIds == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("categoryIds is required"));
            }

            Integer successCount = discountService.assignDiscountToCategories(discountId, categoryIds);

            Map<String, Object> response = new HashMap<>();
            response.put("successCount", successCount);
            response.put("totalCount", categoryIds.size());

            return ResponseEntity.ok(ApiResponse.success(
                    "Successfully assigned " + successCount + " out of " + categoryIds.size() + " categories",
                    response
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/discounts/active")
    public ResponseEntity<ApiResponse<?>> getActiveDiscounts() {
        try {
            List<DiscountDTO> discounts = discountService.getAllActiveDiscounts();
            return ResponseEntity.ok(ApiResponse.success(discounts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/discounts/expired")
    public ResponseEntity<ApiResponse<?>> getExpiredDiscounts() {
        try {
            List<DiscountDTO> discounts = discountService.getAllExpiredDiscounts();
            return ResponseEntity.ok(ApiResponse.success(discounts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/discounts/upcoming")
    public ResponseEntity<ApiResponse<?>> getUpcomingDiscounts() {
        try {
            List<DiscountDTO> discounts = discountService.getAllUpcomingDiscounts();
            return ResponseEntity.ok(ApiResponse.success(discounts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/discounts/product-discounts")
    public ResponseEntity<ApiResponse<?>> getProductDiscounts() {
        try {
            List<DiscountDTO> discounts = discountService.getAllProductDiscounts();
            return ResponseEntity.ok(ApiResponse.success(discounts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/discounts/category-discounts")
    public ResponseEntity<ApiResponse<?>> getCategoryDiscounts() {
        try {
            List<DiscountDTO> discounts = discountService.getAllCategoryDiscounts();
            return ResponseEntity.ok(ApiResponse.success(discounts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/discounts/{id}")
    public ResponseEntity<ApiResponse<?>> deleteDiscount(@PathVariable Integer id) {
        try {
            discountService.deleteDiscount(id);
            return ResponseEntity.ok(ApiResponse.success("Discount deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

   

    // Lấy danh sách sản phẩm đủ điều kiện để áp dụng discount, khi mở admin
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
}
