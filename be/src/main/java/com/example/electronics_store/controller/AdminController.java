package com.example.electronics_store.controller;

import com.example.electronics_store.dto.*;
import com.example.electronics_store.service.*;
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

    @Autowired
    public AdminController(
            UserService userService,
            ProductService productService,
            CategoryService categoryService,
            OrderService orderService,
            DiscountService discountService,
            RatingService ratingService,
            StatisticsService statisticsService) {
        this.userService = userService;
        this.productService = productService;
        this.categoryService = categoryService;
        this.orderService = orderService;
        this.discountService = discountService;
        this.ratingService = ratingService;
        this.statisticsService = statisticsService;
    }

    // User Management
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<?>> getAllUsers() {
        try {
            List<UserDTO> users = userService.getAllUsers();
            return ResponseEntity.ok(ApiResponse.success(users));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/users/admins")
    public ResponseEntity<ApiResponse<?>> getAllAdmins() {
        try {
            List<UserDTO> admins = userService.getAllAdmins();
            return ResponseEntity.ok(ApiResponse.success(admins));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/users/customers")
    public ResponseEntity<ApiResponse<?>> getAllCustomers() {
        try {
            List<UserDTO> customers = userService.getAllCustomers();
            return ResponseEntity.ok(ApiResponse.success(customers));
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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() :
                    Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);
            List<OrderDTO> orders = orderService.getAllOrders();

            return ResponseEntity.ok(ApiResponse.success(orders));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

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

    // Discount Management
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

    @PutMapping("/discounts/{id}")
    public ResponseEntity<ApiResponse<?>> updateDiscount(
            @PathVariable Integer id,
            @Valid @RequestBody DiscountDTO discountDTO) {
        try {
            DiscountDTO discount = discountService.updateDiscount(id, discountDTO);
            return ResponseEntity.ok(ApiResponse.success("Discount updated successfully", discount));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }



    @GetMapping("/discounts")
    public ResponseEntity<ApiResponse<?>> getAllDiscounts() {
        try {
            List<DiscountDTO> discounts = discountService.getAllDiscounts();
            return ResponseEntity.ok(ApiResponse.success(discounts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Rating Management
    @GetMapping("/ratings")
    public ResponseEntity<ApiResponse<?>> getAllRatings() {
        try {
            // Get all ratings from all products
            List<RatingDTO> allRatings = ratingService.getRatingsByProductId(null);
            return ResponseEntity.ok(ApiResponse.success(allRatings));
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
    public ResponseEntity<ApiResponse<?>> getAllCategories() {
        try {
            List<CategoryDTO> categories = categoryService.getAllCategories();
            return ResponseEntity.ok(ApiResponse.success(categories));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Get all products
    @GetMapping("/products")
    public ResponseEntity<ApiResponse<?>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() :
                    Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<ProductDTO> products = productService.getAllProducts(pageable);

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
}
