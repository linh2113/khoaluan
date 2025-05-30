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
        discount.setType(discountDTO.getType());
        discount.setValue(discountDTO.getValue());
        discount.setStartDate(discountDTO.getStartDate());
        discount.setEndDate(discountDTO.getEndDate());
        discount.setIsActive(discountDTO.getIsActive() != null ? discountDTO.getIsActive() : true);
        discount.setPriority(discountDTO.getPriority() != null ? discountDTO.getPriority() : 0);

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

    @Override
    @Transactional
    public DiscountDTO updateDiscount(Integer id, DiscountUpdateDTO discountUpdateDTO) {
        Optional<ProductDiscount> productDiscount = productDiscountRepository.findById(id);
        if (productDiscount.isPresent()) {
            return updateProductDiscount(id, discountUpdateDTO);
        }

        Optional<CategoryDiscount> categoryDiscount = categoryDiscountRepository.findById(id);
        if (categoryDiscount.isPresent()) {
            return updateCategoryDiscount(id, discountUpdateDTO);
        }

        throw new RuntimeException("Discount not found");
    }

    @Override
    public DiscountDTO getDiscountById(Integer id) {
        // Tìm kiếm trong các bảng để xác định loại
        Optional<ProductDiscount> productDiscount = productDiscountRepository.findById(id);
        if (productDiscount.isPresent()) {
            return mapProductDiscountToDTO(productDiscount.get());
        }

        Optional<CategoryDiscount> categoryDiscount = categoryDiscountRepository.findById(id);
        if (categoryDiscount.isPresent()) {
            return mapCategoryDiscountToDTO(categoryDiscount.get());
        }

        throw new RuntimeException("Discount not found");
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
        List<DiscountDTO> activeDiscounts = new ArrayList<>();

        // Thêm product discounts (active dựa trên discount liên kết)
        activeDiscounts.addAll(productDiscountRepository.findAll().stream()
                .filter(pd -> isDiscountActive(pd.getDiscount(), now))
                .map(this::mapProductDiscountToDTO)
                .collect(Collectors.toList()));

        // Thêm category discounts (active dựa trên discount liên kết)
        activeDiscounts.addAll(categoryDiscountRepository.findAll().stream()
                .filter(cd -> isDiscountActive(cd.getDiscount(), now))
                .map(this::mapCategoryDiscountToDTO)
                .collect(Collectors.toList()));

        return activeDiscounts;
    }

    @Override
    public List<DiscountDTO> getAllExpiredDiscounts() {
        LocalDateTime now = LocalDateTime.now();
        List<DiscountDTO> expiredDiscounts = new ArrayList<>();

        // Thêm product discounts (expired dựa trên discount liên kết)
        expiredDiscounts.addAll(productDiscountRepository.findAll().stream()
                .filter(pd -> pd.getDiscount().getEndDate().isBefore(now))
                .map(this::mapProductDiscountToDTO)
                .collect(Collectors.toList()));

        // Thêm category discounts (expired dựa trên discount liên kết)
        expiredDiscounts.addAll(categoryDiscountRepository.findAll().stream()
                .filter(cd -> cd.getDiscount().getEndDate().isBefore(now))
                .map(this::mapCategoryDiscountToDTO)
                .collect(Collectors.toList()));

        return expiredDiscounts;
    }

    @Override
    public List<DiscountDTO> getAllUpcomingDiscounts() {
        LocalDateTime now = LocalDateTime.now();
        List<DiscountDTO> upcomingDiscounts = new ArrayList<>();

        // Thêm product discounts (upcoming dựa trên discount liên kết)
        upcomingDiscounts.addAll(productDiscountRepository.findAll().stream()
                .filter(pd -> pd.getDiscount().getStartDate().isAfter(now))
                .map(this::mapProductDiscountToDTO)
                .collect(Collectors.toList()));

        // Thêm category discounts (upcoming dựa trên discount liên kết)
        upcomingDiscounts.addAll(categoryDiscountRepository.findAll().stream()
                .filter(cd -> cd.getDiscount().getStartDate().isAfter(now))
                .map(this::mapCategoryDiscountToDTO)
                .collect(Collectors.toList()));

        return upcomingDiscounts;
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
        List<ProductDiscount> productDiscounts = productDiscountRepository.findActiveDiscountsByProduct(product, now);
        if (!productDiscounts.isEmpty()) {
            ProductDiscount highestPriorityDiscount = productDiscounts.get(0);
            if (highestPriorityDiscount.getDiscountedPrice() != null) {
                return highestPriorityDiscount.getDiscountedPrice().floatValue();
            } else {
                double discountValue = highestPriorityDiscount.getDiscount().getValue();
                return (float) (product.getPrice() * (1 - discountValue / 100));
            }
        }
        //check category discount
        List<CategoryDiscount> categoryDiscounts = categoryDiscountRepository.findActiveDiscountsByCategory(product.getCategory(), now);
        if (!categoryDiscounts.isEmpty()) {
            CategoryDiscount highestPriorityDiscount = categoryDiscounts.get(0);
            double discountValue = highestPriorityDiscount.getDiscount().getValue();
            return (float) (product.getPrice() * (1 - discountValue / 100));
        }
        // no discount
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
            // Lấy discount có priority cao nhất
            ProductDiscount highestPriorityDiscount = productDiscounts.stream()
                    .sorted(Comparator.comparing(pd -> pd.getDiscount().getPriority(), Comparator.reverseOrder()))
                    .findFirst().orElse(null);

            if (highestPriorityDiscount != null) {
                if (highestPriorityDiscount.getDiscountedPrice() != null) {
                    return highestPriorityDiscount.getDiscountedPrice().floatValue();
                } else {
                    // Tính giá giảm dựa trên phần trăm
                    double discountValue = highestPriorityDiscount.getDiscount().getValue();
                    return (float) (product.getPrice() * (1 - discountValue / 100));
                }
            }
        }
        // Kiểm tra category discount tại thời điểm cụ thể
        List<CategoryDiscount> categoryDiscounts = categoryDiscountRepository.findByCategoryAndTimeRange(product.getCategory().getId(), time);
        if (!categoryDiscounts.isEmpty()) {
            CategoryDiscount highestPriorityDiscount = categoryDiscounts.stream()
                    .sorted(Comparator.comparing(cd -> cd.getDiscount().getPriority(), Comparator.reverseOrder()))
                    .findFirst().orElse(null);
            if (highestPriorityDiscount != null) {
                double discountValue = highestPriorityDiscount.getDiscount().getValue();
                return (float) (product.getPrice() * (1 - discountValue / 100));
            }
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

                // Tìm kiếm theo giá trị giảm giá
                predicates.add(cb.like(cb.lower(root.get("value").as(String.class)), searchTerm));
                // Tìm kiếm theo loại discount
                if ("product".equalsIgnoreCase(search) || "sản phẩm".equalsIgnoreCase(search)) {
                    predicates.add(cb.equal(root.get("type"), Discount.DiscountType.PRODUCT));
                } else if ("category".equalsIgnoreCase(search) || "danh mục".equalsIgnoreCase(search)) {
                    predicates.add(cb.equal(root.get("type"), Discount.DiscountType.CATEGORY));
                }

                // Tìm kiếm theo trạng thái
                if ("active".equalsIgnoreCase(search) || "kích hoạt".equalsIgnoreCase(search) || "true".equalsIgnoreCase(search)) {
                    predicates.add(cb.equal(root.get("isActive"), true));
                } else if ("inactive".equalsIgnoreCase(search) || "không kích hoạt".equalsIgnoreCase(search) || "false".equalsIgnoreCase(search)) {
                    predicates.add(cb.equal(root.get("isActive"), false));
                }

                // Tìm kiếm theo ngày (định dạng YYYY-MM-DD)
                if (search.matches("\\d{4}-\\d{2}(-\\d{2})?")) {
                    predicates.add(cb.like(cb.function("DATE_FORMAT", String.class, root.get("startDate"), cb.literal("%Y-%m-%d")), search + "%"));
                    predicates.add(cb.like(cb.function("DATE_FORMAT", String.class, root.get("endDate"), cb.literal("%Y-%m-%d")), search + "%"));
                    predicates.add(cb.like(cb.function("DATE_FORMAT", String.class, root.get("createdAt"), cb.literal("%Y-%m-%d")), search + "%"));
                    predicates.add(cb.like(cb.function("DATE_FORMAT", String.class, root.get("updatedAt"), cb.literal("%Y-%m-%d")), search + "%"));
                }

                // Tìm kiếm theo trạng thái thời gian
                LocalDateTime now = LocalDateTime.now();
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

                // Tìm kiếm theo độ ưu tiên
                try {
                    Integer priority = Integer.parseInt(search);
                    predicates.add(cb.equal(root.get("priority"), priority));
                } catch (NumberFormatException ignored) {
                }

                return cb.or(predicates.toArray(new Predicate[0]));
            });
        }

        Page<Discount> discountPage = discountRepository.findAll(spec, pageable);
        return discountPage.map(this::mapToDTO);
    }

    private DiscountDTO mapToDTO(Discount discount) {
        // Tìm xem discount này thuộc loại nào
        List<ProductDiscount> productDiscounts = productDiscountRepository.findByDiscountId(discount.getId());
        if (!productDiscounts.isEmpty()) {
            return mapProductDiscountToDTO(productDiscounts.get(0));
        }

        List<CategoryDiscount> categoryDiscounts = categoryDiscountRepository.findByDiscountId(discount.getId());
        if (!categoryDiscounts.isEmpty()) {
            return mapCategoryDiscountToDTO(categoryDiscounts.get(0));
        }

        // Fallback
        return DiscountDTO.builder()
                .id(discount.getId())
                .value(discount.getValue())
                .startDate(discount.getStartDate())
                .endDate(discount.getEndDate())
                .isActive(discount.getIsActive())
                .priority(discount.getPriority())
                .build();
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

        // Lọc ra các sản phẩm đủ điều kiện
        List<Integer> eligibleProductIds = discountEligibilityService.getEligibleProductIds(productIds);

        if (eligibleProductIds.isEmpty()) {
            throw new RuntimeException("No eligible products found");
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
        return productDiscountRepository.findActiveDiscountsByProductId(productId, now).stream()
                .map(this::mapProductDiscountToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public DiscountDTO updateProductDiscount(Integer id, DiscountUpdateDTO discountUpdateDTO) {
        ProductDiscount productDiscount = productDiscountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product discount not found"));

        if (discountUpdateDTO.getDiscountedPrice() != null) {
            productDiscount.setDiscountedPrice(discountUpdateDTO.getDiscountedPrice());
        }

        Discount discount = productDiscount.getDiscount();
        boolean updateDiscount = false;

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
        if (discountUpdateDTO.getPriority() != null && !discountUpdateDTO.getPriority().equals(discount.getPriority())) {
            discount.setPriority(discountUpdateDTO.getPriority());
            updateDiscount = true;
        }

        if (updateDiscount) {
            discountRepository.save(discount);
        }

        ProductDiscount updatedProductDiscount = productDiscountRepository.save(productDiscount);
        return mapProductDiscountToDTO(updatedProductDiscount);
    }


    @Override
    public List<DiscountDTO> getAllProductDiscounts() {
        return productDiscountRepository.findAll().stream()
                .map(this::mapProductDiscountToDTO)
                .collect(Collectors.toList());
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
                if (!discountEligibilityService.isCategoryEligibleForDiscount(categoryId)) {
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
        return categoryDiscountRepository.findActiveDiscountsByCategoryId(categoryId, now).stream()
                .map(this::mapCategoryDiscountToDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    public DiscountDTO updateCategoryDiscount(Integer id, DiscountUpdateDTO discountUpdateDTO) {
        CategoryDiscount categoryDiscount = categoryDiscountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category discount not found"));

        Discount discount = categoryDiscount.getDiscount();
        boolean updateDiscount = false;

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
        if (discountUpdateDTO.getPriority() != null && !discountUpdateDTO.getPriority().equals(discount.getPriority())) {
            discount.setPriority(discountUpdateDTO.getPriority());
            updateDiscount = true;
        }

        if (updateDiscount) {
            discountRepository.save(discount);
        }

        CategoryDiscount updatedCategoryDiscount = categoryDiscountRepository.save(categoryDiscount);
        return mapCategoryDiscountToDTO(updatedCategoryDiscount);
    }


    @Override
    public List<DiscountDTO> getAllCategoryDiscounts() {
        return categoryDiscountRepository.findAll().stream()
                .map(this::mapCategoryDiscountToDTO)
                .collect(Collectors.toList());
    }

    // Helper method to check if a discount is active at a given time
    private boolean isDiscountActive(Discount discount, LocalDateTime time) {
        return discount.getIsActive() &&
                discount.getStartDate().isBefore(time) &&
                discount.getEndDate().isAfter(time);
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
        List<ProductDiscount> productDiscounts = productDiscountRepository.findActiveDiscountsByProduct(product, now);
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
        List<CategoryDiscount> categoryDiscounts = categoryDiscountRepository.findActiveDiscountsByCategory(product.getCategory(), now);
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

    // Helper methods
    private DiscountDTO mapProductDiscountToDTO(ProductDiscount productDiscount) {
        Discount discount = productDiscount.getDiscount();
        LocalDateTime now = LocalDateTime.now();
        boolean isActive = discount.getIsActive() &&
                discount.getStartDate().isBefore(now) &&
                discount.getEndDate().isAfter(now);

        return DiscountDTO.builder()
                .id(productDiscount.getId())
                .type(Discount.DiscountType.PRODUCT)
                .productId(productDiscount.getProduct().getId())
                .productName(productDiscount.getProduct().getName())
                .discountId(discount.getId())
                .value(discount.getValue())
                .discountedPrice(productDiscount.getDiscountedPrice())
                .startDate(discount.getStartDate())
                .endDate(discount.getEndDate())
                .isActive(isActive)
                .priority(discount.getPriority())
                .createdAt(productDiscount.getCreatedAt())
                .build();
    }

    private DiscountDTO mapCategoryDiscountToDTO(CategoryDiscount categoryDiscount) {
        Discount discount = categoryDiscount.getDiscount();
        LocalDateTime now = LocalDateTime.now();
        boolean isActive = discount.getIsActive() &&
                discount.getStartDate().isBefore(now) &&
                discount.getEndDate().isAfter(now);

        return DiscountDTO.builder()
                .id(categoryDiscount.getId())
                .type(Discount.DiscountType.CATEGORY)
                .categoryId(categoryDiscount.getCategory().getId())
                .categoryName(categoryDiscount.getCategory().getCategoryName())
                .discountId(discount.getId())
                .value(discount.getValue())
                .startDate(discount.getStartDate())
                .endDate(discount.getEndDate())
                .isActive(isActive)
                .priority(discount.getPriority())
                .createdAt(categoryDiscount.getCreatedAt())
                .build();
    }
    private DiscountDTO mapToBasicDTO(Discount discount) {
        return DiscountDTO.builder()
                .id(discount.getId())
                .type(discount.getType())
                .value(discount.getValue())
                .startDate(discount.getStartDate())
                .endDate(discount.getEndDate())
                .isActive(discount.getIsActive())
                .priority(discount.getPriority())
                .createdAt(discount.getCreatedAt())
                .updatedAt(discount.getUpdatedAt())
                .assignedCount(0)
                .build();
    }
}