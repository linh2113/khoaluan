package com.example.electronics_store.service.impl;

import com.example.electronics_store.dto.DiscountDTO;
import com.example.electronics_store.dto.DiscountUpdateDTO;
import com.example.electronics_store.model.*;
import com.example.electronics_store.repository.*;
import com.example.electronics_store.service.DiscountService;
import com.example.electronics_store.service.domain.DiscountEligibilityService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;
    private final ProductDiscountRepository productDiscountRepository;
    private final CategoryDiscountRepository categoryDiscountRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FlashSaleItemRepository flashSaleItemRepository;
    private final DiscountEligibilityService discountEligibilityService;

    @Autowired
    public DiscountServiceImpl(
            DiscountRepository discountRepository,
            ProductDiscountRepository productDiscountRepository,
            CategoryDiscountRepository categoryDiscountRepository,
            ProductRepository productRepository,
            CategoryRepository categoryRepository, FlashSaleItemRepository flashSaleItemRepository, DiscountEligibilityService discountEligibilityService) {
        this.discountRepository = discountRepository;
        this.productDiscountRepository = productDiscountRepository;
        this.categoryDiscountRepository = categoryDiscountRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.flashSaleItemRepository = flashSaleItemRepository;
        this.discountEligibilityService = discountEligibilityService;
    }

    @Override
    @Transactional
    public DiscountDTO createDiscount(DiscountDTO discountDTO) {
        // Tạo discount
        Discount discount = new Discount();
        discount.setName(discountDTO.getName());
        discount.setType(discountDTO.getType());
        discount.setValue(discountDTO.getValue());
        discount.setStartDate(discountDTO.getStartDate());
        discount.setEndDate(discountDTO.getEndDate());
        discount.setIsActive(discountDTO.getIsActive() != null ? discountDTO.getIsActive() : true);

        Discount savedDiscount = discountRepository.save(discount);

        DiscountDTO resultDTO = mapToBasicDTO(savedDiscount);

        // Gắn với sản phẩm
        if (discountDTO.getType() == Discount.DiscountType.PRODUCT &&
                discountDTO.getProductIds() != null && !discountDTO.getProductIds().isEmpty()) {
            // Gắn discount với các sản phẩm
            int successCount = assignDiscountToProducts(savedDiscount.getId(),
                    discountDTO.getProductIds(),
                    discountDTO.getDiscountedPrices());
            resultDTO.setAssignedCount(successCount);
            resultDTO.setProductIds(discountDTO.getProductIds());
        }
        //Gắn với danh mục
        else if (discountDTO.getType() == Discount.DiscountType.CATEGORY &&
                discountDTO.getCategoryIds() != null && !discountDTO.getCategoryIds().isEmpty()) {
            // Gắn discount với các danh mục
            int successCount = assignDiscountToCategories(savedDiscount.getId(),
                    discountDTO.getCategoryIds());
            resultDTO.setAssignedCount(successCount);
            resultDTO.setCategoryIds(discountDTO.getCategoryIds());
        }

        return resultDTO;
    }

    //update các trường cơ bản của discount
    @Override
    @Transactional
    public DiscountDTO updateDiscount(Integer id, DiscountUpdateDTO discountUpdateDTO) {
    // Tìm discount trong bảng Discount chính
    Discount discount = discountRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Discount not found with ID: " + id));
    
    // Cập nhật các trường cơ bản của discount
    boolean updateDiscount = false;
    
    if (discountUpdateDTO.getName() != null && !discountUpdateDTO.getName().equals(discount.getName())) {
        discount.setName(discountUpdateDTO.getName());
        updateDiscount = true;
    }
    
    if (discountUpdateDTO.getValue() != null && !discountUpdateDTO.getValue().equals(discount.getValue())) {
        discount.setValue(discountUpdateDTO.getValue());
        updateDiscount = true;
    }
    
    if (discountUpdateDTO.getStartDate() != null && !discountUpdateDTO.getStartDate().equals(discount.getStartDate())) {
        discount.setStartDate(discountUpdateDTO.getStartDate());
        updateDiscount = true;
    }
    
    if (discountUpdateDTO.getEndDate() != null && !discountUpdateDTO.getEndDate().equals(discount.getEndDate())) {
        discount.setEndDate(discountUpdateDTO.getEndDate());
        updateDiscount = true;
    }
    
    if (discountUpdateDTO.getIsActive() != null && !discountUpdateDTO.getIsActive().equals(discount.getIsActive())) {
        discount.setIsActive(discountUpdateDTO.getIsActive());
        updateDiscount = true;
    }
    
    if (updateDiscount) {
        discount.setUpdatedAt(LocalDateTime.now());
        discountRepository.save(discount);
    }
    
    return mapToDTO(discount);
    }


    @Override
    public DiscountDTO getDiscountById(Integer id) {
    Discount discount = discountRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Discount not found with ID: " + id));
    return mapToDTO(discount);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<DiscountDTO> getAllDiscounts(String search, Pageable pageable) {
        if (search != null && !search.trim().isEmpty()) {
            return this.getDiscountsWithSearch(search, pageable);
        } else {
            Page<Discount> discountPage = discountRepository.findAll(pageable);
            return discountPage.map(this::mapToDTO);
        }
    }


    
    @Override
    public List<DiscountDTO> getAllActiveDiscounts() {
    LocalDateTime now = LocalDateTime.now();
    // Lấy tất cả discount đang active
    List<Discount> activeDiscounts = discountRepository.findAllEffectiveDiscounts(now);
    // Map sang DTO
    return activeDiscounts.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<DiscountDTO> getAllExpiredDiscounts() {
        LocalDateTime now = LocalDateTime.now();
    // Lấy tất cả discount đã hết hạn
    List<Discount> expiredDiscounts = discountRepository.findAllExpiredDiscounts(now);
    // Map sang DTO
    return expiredDiscounts.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<DiscountDTO> getAllUpcomingDiscounts() {
        LocalDateTime now = LocalDateTime.now();
    
        // Lấy tất cả discount sắp tới
        List<Discount> upcomingDiscounts = discountRepository.findAllUpcomingDiscounts(now);
    
        // Map sang DTO
        return upcomingDiscounts.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public boolean isDiscountValid(Integer discountId) {
        Optional<Discount> optionalDiscount = discountRepository.findById(discountId);
        if (optionalDiscount.isEmpty()) {
            return false;
        }

        Discount discount = optionalDiscount.get();
        LocalDateTime now = LocalDateTime.now();

        return discount.getIsActive() &&
                discount.getStartDate().isBefore(now) &&
                discount.getEndDate().isAfter(now);
    }

    @Override
    public Float calculateProductPrice(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        LocalDateTime now = LocalDateTime.now();
        
        // Kiểm tra flash sale trước
        Optional<FlashSaleItem> flashSaleItem = flashSaleItemRepository.findActiveFlashSaleItemByProductId(product.getId(), now);
        if (flashSaleItem.isPresent()) {
            return flashSaleItem.get().getFlashPrice().floatValue();
        }
        
        // Kiểm tra product discount
        List<ProductDiscount> productDiscounts = productDiscountRepository.findEffectiveDiscountsByProduct(product, now);
        if (!productDiscounts.isEmpty()) {
            ProductDiscount productDiscount = productDiscounts.get(0);
            if (productDiscount.getDiscountedPrice() != null) {
                return productDiscount.getDiscountedPrice().floatValue();
            } else {
                double discountValue = productDiscount.getDiscount().getValue();
                return (float) (product.getPrice() * (1 - discountValue / 100));
            }
        }
        
        // Kiểm tra category discount
        List<CategoryDiscount> categoryDiscounts = categoryDiscountRepository.findEffectiveDiscountsByCategory(product.getCategory(), now);
        if (!categoryDiscounts.isEmpty()) {
            CategoryDiscount categoryDiscount = categoryDiscounts.get(0);
            double discountValue = categoryDiscount.getDiscount().getValue();
            return (float) (product.getPrice() * (1 - discountValue / 100));
        }
        
        // Không có giảm giá
        return product.getPrice().floatValue();
    }

    @Override
    public Float calculateProductPriceAtTime(Integer productId, LocalDateTime time) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Kiểm tra flash sale tại thời điểm cụ thể
        Optional<FlashSaleItem> flashSaleItem = flashSaleItemRepository.findByProductAndTimeRange(product.getId(), time);
        if (flashSaleItem.isPresent()) {
            return flashSaleItem.get().getFlashPrice().floatValue();
        }

        // Kiểm tra product discount tại thời điểm cụ thể
        List<ProductDiscount> productDiscounts = productDiscountRepository.findByProductAndTimeRange(product.getId(), time);
        if (!productDiscounts.isEmpty()) {
            ProductDiscount productDiscount = productDiscounts.get(0);
            if (productDiscount.getDiscountedPrice() != null) {
                return productDiscount.getDiscountedPrice().floatValue();
            } else {
                double discountValue = productDiscount.getDiscount().getValue();
                return (float) (product.getPrice() * (1 - discountValue / 100));
            }
        }
        
        // Kiểm tra category discount tại thời điểm cụ thể
        List<CategoryDiscount> categoryDiscounts = categoryDiscountRepository.findByCategoryAndTimeRange(product.getCategory().getId(), time);
        if (!categoryDiscounts.isEmpty()) {
            CategoryDiscount categoryDiscount = categoryDiscounts.get(0);
            double discountValue = categoryDiscount.getDiscount().getValue();
            return (float) (product.getPrice() * (1 - discountValue / 100));
        }
        
        return product.getPrice().floatValue();
    }




    @Transactional(readOnly = true)
    public Page<DiscountDTO> getDiscountsWithSearch(String search, Pageable pageable) {
        Specification<Discount> spec = Specification.where(null);
        if (search != null && !search.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                Integer discountId = null;
                try {
                    discountId = Integer.parseInt(search);
                    predicates.add(cb.equal(root.get("id"), discountId));
                } catch (NumberFormatException ignored) {
                }

                String searchTerm = "%" + search.toLowerCase() + "%";
                LocalDateTime now = LocalDateTime.now();
                // Tìm kiếm theo tên discount
                predicates.add(cb.like(cb.lower(root.get("name")), searchTerm));
                
                // Tìm kiếm theo giá trị giảm giá
                predicates.add(cb.like(cb.lower(root.get("value").as(String.class)), searchTerm));
                // Tìm kiếm theo loại discount
                if ("product".equalsIgnoreCase(search) || "sản phẩm".equalsIgnoreCase(search)) {
                    predicates.add(cb.equal(root.get("type"), Discount.DiscountType.PRODUCT));
                } else if ("category".equalsIgnoreCase(search) || "danh mục".equalsIgnoreCase(search)) {
                    predicates.add(cb.equal(root.get("type"), Discount.DiscountType.CATEGORY));
                }
                // tìm kiếm theo trạng thái admin
                if ("active".equalsIgnoreCase(search) || "kích hoạt".equalsIgnoreCase(search) || "true".equalsIgnoreCase(search)) {
                predicates.add(cb.equal(root.get("isActive"), true));
                } else if ("inactive".equalsIgnoreCase(search) || "không kích hoạt".equalsIgnoreCase(search) || "false".equalsIgnoreCase(search)) {
                predicates.add(cb.equal(root.get("isActive"), false));
                }

                // tìm kiếm theo trạng thái hiệu lực
                if ("effective".equalsIgnoreCase(search) || "hiệu lực".equalsIgnoreCase(search)) {
                predicates.add(cb.and(
                    cb.isTrue(root.get("isActive")),
                    cb.lessThanOrEqualTo(root.get("startDate"), now),
                    cb.greaterThanOrEqualTo(root.get("endDate"), now)
                ));
                } else if ("ineffective".equalsIgnoreCase(search) || "không hiệu lực".equalsIgnoreCase(search)) {
                predicates.add(cb.or(
                    cb.isFalse(root.get("isActive")),
                    cb.greaterThan(root.get("startDate"), now),
                    cb.lessThan(root.get("endDate"), now)
                ));
                }

                
                // Tìm kiếm theo ngày (định dạng YYYY-MM-DD)
                if (search.matches("\\d{4}-\\d{2}(-\\d{2})?")) {
                    predicates.add(cb.like(cb.function("DATE_FORMAT", String.class, root.get("startDate"), cb.literal("%Y-%m-%d")), search + "%"));
                    predicates.add(cb.like(cb.function("DATE_FORMAT", String.class, root.get("endDate"), cb.literal("%Y-%m-%d")), search + "%"));
                    predicates.add(cb.like(cb.function("DATE_FORMAT", String.class, root.get("createdAt"), cb.literal("%Y-%m-%d")), search + "%"));
                    predicates.add(cb.like(cb.function("DATE_FORMAT", String.class, root.get("updatedAt"), cb.literal("%Y-%m-%d")), search + "%"));
                }

                // Tìm kiếm theo trạng thái thời gian
                if ("expired".equalsIgnoreCase(search) || "hết hạn".equalsIgnoreCase(search)) {
                    predicates.add(cb.lessThan(root.get("endDate"), now));
                } else if ("upcoming".equalsIgnoreCase(search) || "sắp tới".equalsIgnoreCase(search)) {
                    predicates.add(cb.greaterThan(root.get("startDate"), now));
                } else if ("current".equalsIgnoreCase(search) || "hiện tại".equalsIgnoreCase(search)) {
                    predicates.add(cb.and(
                            cb.lessThanOrEqualTo(root.get("startDate"), now),
                            cb.greaterThanOrEqualTo(root.get("endDate"), now)
                    ));
                }
                return cb.or(predicates.toArray(new Predicate[0]));
            });
        }
        Page<Discount> discountPage = discountRepository.findAll(spec, pageable);
        return discountPage.map(this::mapToBasicDTO);
    }
    
    
    @Override
    public Optional<Discount> getDiscountEntityById(Integer id) {
        return discountRepository.findById(id);
    }

    @Override
    @Transactional
    public Integer assignDiscountToProducts(Integer discountId, List<Integer> productIds, Map<Integer, Integer> discountedPrices) {
        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new RuntimeException("Discount not found"));

        // Kiểm tra loại discount
        if (discount.getType() != Discount.DiscountType.PRODUCT) {
            throw new RuntimeException("This discount is not applicable to products");
        }

        // Kiểm tra discount còn hạn hay không
        LocalDateTime now = LocalDateTime.now();
        if (discount.getEndDate().isBefore(now)) {
            throw new RuntimeException("This discount has expired");
        }

         // Lọc ra các sản phẩm không có discount chồng chéo thời gian
        List<Integer> eligibleProductIds = discountEligibilityService.getEligibleProductIds(
                productIds, discount.getStartDate(), discount.getEndDate());
         if (eligibleProductIds.isEmpty()) {
            throw new RuntimeException("No eligible products found. All products already have discounts in this time period.");
        }

        int successCount = 0;
        List<ProductDiscount> newProductDiscounts = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        // Xử lý từng sản phẩm đủ điều kiện
        for (Integer productId : eligibleProductIds) {
            try {
                // Kiểm tra sản phẩm có tồn tại không
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product ID " + productId + " not found"));

                // Tạo ProductDiscount mới
                ProductDiscount productDiscount = new ProductDiscount();
                productDiscount.setProduct(product);
                productDiscount.setDiscount(discount);
                productDiscount.setCreatedAt(LocalDateTime.now());

                // Thiết lập giá đã giảm nếu có
                if (discountedPrices != null && discountedPrices.containsKey(productId)) {
                    productDiscount.setDiscountedPrice(discountedPrices.get(productId));
                }else {
                // Tự động tính giá giảm dựa trên giá gốc và phần trăm giảm giá
                double discountValue = discount.getValue();
                int calculatedPrice = (int) Math.round(product.getPrice() * (1 - discountValue / 100));
                productDiscount.setDiscountedPrice(calculatedPrice);
                }
                

                newProductDiscounts.add(productDiscount);
                successCount++;
            } catch (Exception e) {
                errors.add("Error processing product ID " + productId + ": " + e.getMessage());
            }
        }
        // Lưu tất cả các ProductDiscount mới
        if (!newProductDiscounts.isEmpty()) {
            productDiscountRepository.saveAll(newProductDiscounts);
        }
        return successCount;
    }


    //Phương thức thêm
    @Override
    public List<DiscountDTO> getProductDiscountsByProductId(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return productDiscountRepository.findByProduct(product).stream()
                .map(this::mapProductDiscountToDTO)
                .collect(Collectors.toList());
    }



    @Override
    public List<DiscountDTO> getActiveProductDiscountsByProductId(Integer productId) {
        LocalDateTime now = LocalDateTime.now();
        return productDiscountRepository.findEffectiveDiscountsByProductId(productId, now).stream()
                .map(this::mapProductDiscountToDTO)
                .collect(Collectors.toList());
    }
    

    @Override
    public List<DiscountDTO> getAllProductDiscounts() {
        return productDiscountRepository.findAll().stream()
                .map(this::mapProductDiscountToDTO)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public Integer removeProductsFromDiscount(Integer discountId, List<Integer> productIds) {
    int successCount = 0;
    List<String> errors = new ArrayList<>();
    
    // Xử lý từng sản phẩm
    for (Integer productId : productIds) {
        try {
            // Tìm product discount
            Optional<ProductDiscount> productDiscount = productDiscountRepository.findByDiscountIdAndProductId(discountId, productId);
            if (productDiscount.isPresent()) {
                // Xóa product discount
                productDiscountRepository.delete(productDiscount.get());
                successCount++;
            } else {
                errors.add("Product ID " + productId + " not found in this discount");
            }
        } catch (Exception e) {
            errors.add("Error processing product ID " + productId + ": " + e.getMessage());
        }
    }
    
    // Nếu không có sản phẩm nào được xóa thành công
    if (successCount == 0 && !errors.isEmpty()) {
        throw new RuntimeException("Failed to remove products: " + String.join(", ", errors));
    }
    
    return successCount;
    }

    @Override
    @Transactional
    public Integer updateProductDiscountPrices(Integer discountId, Map<Integer, Integer> productPrices) {
    int successCount = 0;
    List<String> errors = new ArrayList<>();
    List<ProductDiscount> updatedDiscounts = new ArrayList<>();
    
    // Xử lý từng sản phẩm
    for (Map.Entry<Integer, Integer> entry : productPrices.entrySet()) {
        Integer productId = entry.getKey();
        Integer discountedPrice = entry.getValue();
        
        try {
            // Tìm product discount
            Optional<ProductDiscount> optionalProductDiscount = productDiscountRepository.findByDiscountIdAndProductId(discountId, productId);
            
            if (optionalProductDiscount.isPresent()) {
                ProductDiscount productDiscount = optionalProductDiscount.get();
                // Cập nhật giá đã giảm
                productDiscount.setDiscountedPrice(discountedPrice);
                updatedDiscounts.add(productDiscount);
                successCount++;
            } else {
                errors.add("Product ID " + productId + " not found in this discount");
            }
        } catch (Exception e) {
            errors.add("Error processing product ID " + productId + ": " + e.getMessage());
        }
    }
    
    // Lưu tất cả các thay đổi
    if (!updatedDiscounts.isEmpty()) {
        productDiscountRepository.saveAll(updatedDiscounts);
    }
    
    // Nếu không có sản phẩm nào được cập nhật thành công
    if (successCount == 0 && !errors.isEmpty()) {
        throw new RuntimeException("Failed to update product prices: " + String.join(", ", errors));
    }
    
    return successCount;
    }


    @Override
    @Transactional
    public Integer assignDiscountToCategories(Integer discountId, List<Integer> categoryIds) {
        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new RuntimeException("Discount not found"));

        // Kiểm tra loại discount
        if (discount.getType() != Discount.DiscountType.CATEGORY) {
            throw new RuntimeException("This discount is not applicable to categories");
        }

        int successCount = 0;
        List<CategoryDiscount> newCategoryDiscounts = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        // Lấy danh sách danh mục đã có discount này
        List<CategoryDiscount> existingCategoryDiscounts = categoryDiscountRepository.findByDiscountId(discountId);
        Set<Integer> existingCategoryIds = existingCategoryDiscounts.stream()
                .map(cd -> cd.getCategory().getId())
                .collect(Collectors.toSet());
        for (Integer categoryId : categoryIds) {
            try {
                // Bỏ qua nếu danh mục đã có discount này
                if (existingCategoryIds.contains(categoryId)) {
                    errors.add("Category ID " + categoryId + " already has this discount");
                    continue;
                }
                // Sử dụng domain service để kiểm tra eligibility
                if (!discountEligibilityService.isCategoryEligibleForDiscount(categoryId, discount.getStartDate(), discount.getEndDate())) {
                    errors.add("Category ID " + categoryId + " is not eligible for discount");
                    continue;
                }

                // Kiểm tra danh mục có tồn tại không
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new RuntimeException("Category ID " + categoryId + " not found"));

                // Tạo CategoryDiscount mới
                CategoryDiscount categoryDiscount = new CategoryDiscount();
                categoryDiscount.setCategory(category);
                categoryDiscount.setDiscount(discount);
                categoryDiscount.setCreatedAt(LocalDateTime.now());

                newCategoryDiscounts.add(categoryDiscount);
                successCount++;
            } catch (Exception e) {
                errors.add("Error processing category ID " + categoryId + ": " + e.getMessage());
            }
        }

        // Lưu tất cả các CategoryDiscount mới
        if (!newCategoryDiscounts.isEmpty()) {
            categoryDiscountRepository.saveAll(newCategoryDiscounts);
        }
        // Ghi log các lỗi nếu có
        if (!errors.isEmpty()) {
            System.out.println("Errors during batch assignment: " + String.join(", ", errors));
        }
        return successCount;
    }

    //Phương thức thêm
    @Override
    public List<DiscountDTO> getCategoryDiscountsByCategoryId(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return categoryDiscountRepository.findByCategory(category).stream()
                .map(this::mapCategoryDiscountToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DiscountDTO> getActiveCategoryDiscountsByCategoryId(Integer categoryId) {
        LocalDateTime now = LocalDateTime.now();
        return categoryDiscountRepository.findEffectiveDiscountsByCategoryId(categoryId, now).stream()
                .map(this::mapCategoryDiscountToDTO)
                .collect(Collectors.toList());
    }


    @Override
    public List<DiscountDTO> getAllCategoryDiscounts() {
        return categoryDiscountRepository.findAll().stream()
                .map(this::mapCategoryDiscountToDTO)
                .collect(Collectors.toList());
    }


    @Override
    public Map<String, Object> getProductDiscountInfo(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Map<String, Object> result = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();

        // Tính giá sau khi giảm giá
        Float discountedPrice = calculateProductPrice(productId);
        result.put("discountedPrice", discountedPrice.intValue());

        // Tính phần trăm giảm giá
        if (discountedPrice < product.getPrice()) {
            double originalPrice = product.getPrice();
            double salePrice = discountedPrice;
            double discountPercentage = ((originalPrice - salePrice) / originalPrice) * 100;
            result.put("discountPercentage", Math.round(discountPercentage * 10.0) / 10.0);
        } else {
            result.put("discountPercentage", null);
        }

        // Kiểm tra flash sale trước
        Optional<FlashSaleItem> flashSaleItem = flashSaleItemRepository.findActiveFlashSaleItemByProductId(productId, now);
        if (flashSaleItem.isPresent()) {
            result.put("discountType", "FLASH_SALE");
            FlashSale flashSale = flashSaleItem.get().getFlashSale();
            result.put("discountId", flashSale.getId());
            result.put("discountStartDate", flashSale.getStartTime());
            result.put("discountEndDate", flashSale.getEndTime());
            result.put("isDiscountActive", true);
            return result;
        }

        // Kiểm tra product discount
        List<ProductDiscount> productDiscounts = productDiscountRepository.findEffectiveDiscountsByProduct(product, now);
        if (!productDiscounts.isEmpty()) {
            ProductDiscount highestPriorityDiscount = productDiscounts.get(0);
            result.put("discountType", "PRODUCT_DISCOUNT");
            Discount discount = highestPriorityDiscount.getDiscount();
            result.put("discountId", discount.getId());
            result.put("discountStartDate", discount.getStartDate());
            result.put("discountEndDate", discount.getEndDate());
            result.put("isDiscountActive", discount.getIsActive());
            return result;
        }

        // Kiểm tra category discount
        List<CategoryDiscount> categoryDiscounts = categoryDiscountRepository.findEffectiveDiscountsByCategory(product.getCategory(), now);
        if (!categoryDiscounts.isEmpty()) {
            CategoryDiscount highestPriorityDiscount = categoryDiscounts.get(0);
            result.put("discountType", "CATEGORY_DISCOUNT");
            Discount discount = highestPriorityDiscount.getDiscount();
            result.put("discountId", discount.getId());
            result.put("discountStartDate", discount.getStartDate());
            result.put("discountEndDate", discount.getEndDate());
            result.put("isDiscountActive", discount.getIsActive());
            return result;
        }

        // Không có giảm giá
        result.put("discountType", null);
        result.put("discountId", null);
        result.put("discountStartDate", null);
        result.put("discountEndDate", null);
        result.put("isDiscountActive", null);

        return result;
    }
    //Phương thức xóa hàng loạt category từ discount 
    @Override
    @Transactional
    public Integer removeCategoriesFromDiscount(Integer discountId, List<Integer> categoryIds) {
    int successCount = 0;
    List<String> errors = new ArrayList<>();
    
    // Xử lý từng danh mục
    for (Integer categoryId : categoryIds) {
        try {
            // Tìm category discount
            Optional<CategoryDiscount> categoryDiscount = categoryDiscountRepository.findByDiscountIdAndCategoryId(discountId, categoryId);
            
            if (categoryDiscount.isPresent()) {
                // Xóa category discount
                categoryDiscountRepository.delete(categoryDiscount.get());
                successCount++;
            } else {
                errors.add("Category ID " + categoryId + " not found in this discount");
            }
        } catch (Exception e) {
            errors.add("Error processing category ID " + categoryId + ": " + e.getMessage());
        }
    }
    // Nếu không có danh mục nào được xóa thành công
    if (successCount == 0 && !errors.isEmpty()) {
        throw new RuntimeException("Failed to remove categories: " + String.join(", ", errors));
    }
    return successCount;
    }
    



    private boolean isDiscountEffective(Discount discount, LocalDateTime time) {
    return discount.getIsActive() && // Admin cho phép active
            discount.getStartDate().isBefore(time) && // Đã đến thời điểm bắt đầu
            discount.getEndDate().isAfter(time); // Chưa hết hạn
    }

    private DiscountDTO mapToDTO(Discount discount) {
    // Kiểm tra xem discount này thuộc loại nào
    if (discount.getType() == Discount.DiscountType.PRODUCT) {
        return mapDiscountWithProducts(discount);
    } else if (discount.getType() == Discount.DiscountType.CATEGORY) {
        return mapDiscountWithCategories(discount);
    } else {
        return mapToBasicDTO(discount);
    }
    }

    // Phương thức map cho Discount với danh sách sản phẩm
    private DiscountDTO mapDiscountWithProducts(Discount discount) {
    DiscountDTO baseDTO = mapToBasicDTO(discount);
    
    // Lấy tất cả ProductDiscount liên quan đến discount này
    List<ProductDiscount> productDiscounts = productDiscountRepository.findByDiscountId(discount.getId());
    
    // Tạo danh sách productIds và discountedPrices
    List<Integer> productIds = new ArrayList<>();
    Map<Integer, Integer> discountedPrices = new HashMap<>();
    List<String> productNames = new ArrayList<>();
    
    for (ProductDiscount pd : productDiscounts) {
        Product product = pd.getProduct();
        productIds.add(product.getId());
        productNames.add(product.getName());
        if (pd.getDiscountedPrice() != null) {
            discountedPrices.put(product.getId(), pd.getDiscountedPrice());
        }
    }
    
    // Cập nhật DTO với thông tin sản phẩm
    baseDTO.setProductIds(productIds);
    baseDTO.setProductNames(productNames);
    baseDTO.setDiscountedPrices(discountedPrices);
    baseDTO.setAssignedCount(productDiscounts.size());
    
    return baseDTO;
    }

    // Phương thức map cho Discount với danh sách danh mục
    private DiscountDTO mapDiscountWithCategories(Discount discount) {
    DiscountDTO baseDTO = mapToBasicDTO(discount);
    
    // Lấy tất cả CategoryDiscount liên quan đến discount này
    List<CategoryDiscount> categoryDiscounts = categoryDiscountRepository.findByDiscountId(discount.getId());
    
    // Tạo danh sách categoryIds và categoryNames
    List<Integer> categoryIds = new ArrayList<>();
    List<String> categoryNames = new ArrayList<>();
    
    for (CategoryDiscount cd : categoryDiscounts) {
        Category category = cd.getCategory();
        categoryIds.add(category.getId());
        categoryNames.add(category.getCategoryName());
    }
    
    // Cập nhật DTO với thông tin danh mục
    baseDTO.setCategoryIds(categoryIds);
    baseDTO.setCategoryNames(categoryNames);
    baseDTO.setAssignedCount(categoryDiscounts.size());
    
    return baseDTO;
    }


    // Map cho ProductDiscount
    private DiscountDTO mapProductDiscountToDTO(ProductDiscount productDiscount) {
        Discount discount = productDiscount.getDiscount();
        Product product = productDiscount.getProduct();
    
        LocalDateTime now = LocalDateTime.now();
        boolean isActive = isDiscountEffective(discount, now);

    // Tạo danh sách productIds chỉ chứa ID của sản phẩm hiện tại
    List<Integer> productIds = new ArrayList<>();
    productIds.add(product.getId());
    
    // Tạo map discountedPrices nếu có giá đã giảm
    Map<Integer, Integer> discountedPrices = null;
    if (productDiscount.getDiscountedPrice() != null) {
        discountedPrices = new HashMap<>();
        discountedPrices.put(product.getId(), productDiscount.getDiscountedPrice());
    }

    return DiscountDTO.builder()
            .id(productDiscount.getId())
            .name(discount.getName())
            .type(Discount.DiscountType.PRODUCT)
            .productId(product.getId())
            .productName(product.getName())
            .discountId(discount.getId())
            .value(discount.getValue())
            .discountedPrice(productDiscount.getDiscountedPrice())
            .startDate(discount.getStartDate())
            .endDate(discount.getEndDate())
            .isActive(isActive)
            .isEffective(isActive)
            .createdAt(productDiscount.getCreatedAt())
            .productIds(productIds)
            .discountedPrices(discountedPrices)
            .build();
    }
    // Map cho CategoryDiscount
    private DiscountDTO mapCategoryDiscountToDTO(CategoryDiscount categoryDiscount) {
        Discount discount = categoryDiscount.getDiscount();
        Category category = categoryDiscount.getCategory();
    
        LocalDateTime now = LocalDateTime.now();
        boolean isDiscountEffective = isDiscountEffective(discount, now);

        // Tạo danh sách categoryIds chỉ chứa ID của danh mục hiện tại
        List<Integer> categoryIds = new ArrayList<>();
        categoryIds.add(category.getId());

    return DiscountDTO.builder()
            .id(categoryDiscount.getId())
            .name(discount.getName())
            .type(Discount.DiscountType.CATEGORY)
            .categoryId(category.getId())
            .categoryName(category.getCategoryName())
            .discountId(discount.getId())
            .value(discount.getValue())
            .startDate(discount.getStartDate())
            .endDate(discount.getEndDate())
            .isActive(discount.getIsActive())
            .isEffective(isDiscountEffective)
            .createdAt(categoryDiscount.getCreatedAt())
            .categoryIds(categoryIds)
            .build();
    }

    private DiscountDTO mapToBasicDTO(Discount discount) {
        LocalDateTime now = LocalDateTime.now();
        boolean isEffective = isDiscountEffective(discount, now);
        return DiscountDTO.builder()
            .id(discount.getId())
            .name(discount.getName())
            .type(discount.getType())
            .value(discount.getValue())
            .startDate(discount.getStartDate())
            .endDate(discount.getEndDate())
            .isActive(discount.getIsActive())
            .isEffective(isEffective)
            .createdAt(discount.getCreatedAt())
            .updatedAt(discount.getUpdatedAt())
            .build();
}
}