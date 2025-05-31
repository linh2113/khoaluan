package com.example.electronics_store.service.impl;

import com.example.electronics_store.dto.FlashSaleDTO;
import com.example.electronics_store.dto.FlashSaleItemDTO;
import com.example.electronics_store.dto.ProductDTO;
import com.example.electronics_store.model.FlashSale;
import com.example.electronics_store.model.FlashSaleItem;
import com.example.electronics_store.model.Product;
import com.example.electronics_store.repository.FlashSaleRepository;
import com.example.electronics_store.repository.FlashSaleItemRepository;
import com.example.electronics_store.repository.ProductRepository;
import com.example.electronics_store.service.FlashSaleService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FlashSaleServiceImpl implements FlashSaleService {

    @Autowired
    private FlashSaleRepository flashSaleRepository;

    @Autowired
    private FlashSaleItemRepository flashSaleItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional
    public FlashSaleDTO createFlashSale(FlashSaleDTO flashSaleDTO) {
        // Validate thời gian
        validateFlashSaleTime(flashSaleDTO.getStartTime(), flashSaleDTO.getEndTime());

        // Kiểm tra xem có flash sale nào đang hoạt động trong khoảng thời gian này không
        if (flashSaleRepository.existsActiveFlashSaleInTimeRange(
                flashSaleDTO.getStartTime(), flashSaleDTO.getEndTime())) {
            throw new RuntimeException("There is already an active flash sale in this time range");
        }

        FlashSale flashSale = new FlashSale();
        flashSale.setName(flashSaleDTO.getName());
        flashSale.setDescription(flashSaleDTO.getDescription());
        flashSale.setStartTime(flashSaleDTO.getStartTime());
        flashSale.setEndTime(flashSaleDTO.getEndTime());

        FlashSale savedFlashSale = flashSaleRepository.save(flashSale);
        return mapFlashSaleToDTO(savedFlashSale);
    }

    @Override
    @Transactional
    public FlashSaleDTO updateFlashSale(Integer id, FlashSaleDTO flashSaleDTO) {
        FlashSale flashSale = flashSaleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flash sale not found"));

        // Validate thời gian
        validateFlashSaleTime(flashSaleDTO.getStartTime(), flashSaleDTO.getEndTime());

        // Kiểm tra xem flash sale đã bắt đầu chưa
        LocalDateTime now = LocalDateTime.now();
        if (flashSale.getStartTime().isBefore(now)) {
            throw new RuntimeException("Cannot update flash sale that has already started");
        }
        // Chỉ cập nhật thời gian nếu cả hai trường đều được cung cấp
        if (flashSaleDTO.getStartTime() != null && flashSaleDTO.getEndTime() != null) {
        // Validate thời gian
            validateFlashSaleTime(flashSaleDTO.getStartTime(), flashSaleDTO.getEndTime());

        // Kiểm tra xem có flash sale nào khác đang hoạt động trong khoảng thời gian mới không
        if (!flashSale.getStartTime().equals(flashSaleDTO.getStartTime()) ||
            !flashSale.getEndTime().equals(flashSaleDTO.getEndTime())) {
            if (flashSaleRepository.existsActiveFlashSaleInTimeRange(
                    flashSaleDTO.getStartTime(), flashSaleDTO.getEndTime())) {
                throw new IllegalArgumentException("There is already another flash sale in this time range");
            }
        }
        flashSale.setStartTime(flashSaleDTO.getStartTime());
        flashSale.setEndTime(flashSaleDTO.getEndTime());
    }

        // Chỉ cập nhật các trường nếu chúng được cung cấp
        if (flashSaleDTO.getName() != null) {
            flashSale.setName(flashSaleDTO.getName());
        }

        if (flashSaleDTO.getDescription() != null) {
            flashSale.setDescription(flashSaleDTO.getDescription());
        }

    FlashSale updatedFlashSale = flashSaleRepository.save(flashSale);
    return mapFlashSaleToDTO(updatedFlashSale);
}
    @Override
    @Transactional(readOnly = true)
    public FlashSaleDTO getFlashSaleById(Integer id) {
        FlashSale flashSale = flashSaleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flash sale not found"));
        return mapFlashSaleToDTO(flashSale);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashSaleDTO> getAllFlashSales() {
        return flashSaleRepository.findAll().stream()
                .map(this::mapFlashSaleToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FlashSaleDTO> getFlashSalesWithSearch(String search, Pageable pageable) {
    Specification<FlashSale> spec = Specification.where(null);

    if (search != null && !search.trim().isEmpty()) {
        String searchTerm = "%" + search.toLowerCase() + "%";
        spec = spec.and((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Tìm theo ID
            try {
                Integer flashSaleId = Integer.parseInt(search);
                predicates.add(cb.equal(root.get("id"), flashSaleId));
            } catch (NumberFormatException ignored) {
            }

            // Tìm theo tên và mô tả
            predicates.add(cb.like(cb.lower(root.get("name")), searchTerm));
            predicates.add(cb.like(cb.lower(root.get("description")), searchTerm));

            // Tìm theo trạng thái
            LocalDateTime now = LocalDateTime.now();
            if ("active".equalsIgnoreCase(search) || "current".equalsIgnoreCase(search)) {
                predicates.add(cb.and(
                    cb.lessThanOrEqualTo(root.get("startTime"), now),
                    cb.greaterThanOrEqualTo(root.get("endTime"), now)
                ));
            } else if ("upcoming".equalsIgnoreCase(search) || "future".equalsIgnoreCase(search)) {
                predicates.add(cb.greaterThan(root.get("startTime"), now));
            } else if ("past".equalsIgnoreCase(search) || "ended".equalsIgnoreCase(search)) {
                predicates.add(cb.lessThan(root.get("endTime"), now));
            }

            // Tìm theo ngày (định dạng yyyy-MM-dd)
            if (search.matches("\\d{4}-\\d{2}-\\d{2}")) {
                predicates.add(cb.like(
                    cb.function("DATE_FORMAT", String.class, root.get("startTime"), cb.literal("%Y-%m-%d")),
                    search
                ));
                predicates.add(cb.like(
                    cb.function("DATE_FORMAT", String.class, root.get("endTime"), cb.literal("%Y-%m-%d")),
                    search
                ));
            }

            return cb.or(predicates.toArray(new Predicate[0]));
        });
    }

    Page<FlashSale> flashSalePage = flashSaleRepository.findAll(spec, pageable);
    return flashSalePage.map(this::mapFlashSaleToDTO);
    }

    @Override
    public List<FlashSaleDTO> getCurrentFlashSales() {
        LocalDateTime now = LocalDateTime.now();
        return flashSaleRepository.findCurrentFlashSales(now).stream()
                .map(this::mapFlashSaleToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FlashSaleDTO> getUpcomingFlashSales() {
        LocalDateTime now = LocalDateTime.now();
        return flashSaleRepository.findUpcomingFlashSales(now).stream()
                .map(this::mapFlashSaleToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FlashSaleDTO> getPastFlashSales() {
        LocalDateTime now = LocalDateTime.now();
        return flashSaleRepository.findPastFlashSales(now).stream()
                .map(this::mapFlashSaleToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteFlashSale(Integer id) {
        FlashSale flashSale = flashSaleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flash sale not found"));

        LocalDateTime now = LocalDateTime.now();
        if (flashSale.getStartTime().isBefore(now) && flashSale.getEndTime().isAfter(now)) {
            throw new RuntimeException("Cannot delete active flash sale");
        }

        flashSaleRepository.deleteById(id);
    }

    @Override
    @Transactional
    public FlashSaleItemDTO addProductToFlashSale(Integer flashSaleId, FlashSaleItemDTO flashSaleItemDTO) {
        FlashSale flashSale = flashSaleRepository.findById(flashSaleId)
                .orElseThrow(() -> new RuntimeException("Flash sale not found"));

        Product product = productRepository.findById(flashSaleItemDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Kiểm tra xem flash sale đã bắt đầu chưa
        LocalDateTime now = LocalDateTime.now();
        if (flashSale.getStartTime().isBefore(now)) {
            throw new RuntimeException("Cannot add product to flash sale that has already started");
        }

        // Kiểm tra xem sản phẩm đã có trong flash sale này chưa
        boolean productExists = flashSaleItemRepository.findByFlashSaleId(flashSaleId).stream()
                .anyMatch(item -> item.getProduct().getId().equals(flashSaleItemDTO.getProductId()));

        if (productExists) {
            throw new RuntimeException("Product already exists in this flash sale");
        }

        // Validate flash price
        if (flashSaleItemDTO.getFlashPrice() >= product.getPrice()) {
            throw new RuntimeException("Flash price must be lower than original price");
        }

        FlashSaleItem flashSaleItem = new FlashSaleItem();
        flashSaleItem.setFlashSale(flashSale);
        flashSaleItem.setProduct(product);
        flashSaleItem.setFlashPrice(flashSaleItemDTO.getFlashPrice());
        flashSaleItem.setStockLimit(flashSaleItemDTO.getStockLimit());
        flashSaleItem.setSoldCount(0);

        FlashSaleItem savedItem = flashSaleItemRepository.save(flashSaleItem);
        return mapFlashSaleItemToDTO(savedItem);
    }

    @Override
    @Transactional
    public FlashSaleItemDTO updateFlashSaleItem(Integer flashSaleItemId, FlashSaleItemDTO flashSaleItemDTO) {
        FlashSaleItem flashSaleItem = flashSaleItemRepository.findById(flashSaleItemId)
                .orElseThrow(() -> new RuntimeException("Flash sale item not found"));

        // Kiểm tra xem flash sale đã bắt đầu chưa
        LocalDateTime now = LocalDateTime.now();
        if (flashSaleItem.getFlashSale().getStartTime().isBefore(now)) {
            throw new RuntimeException("Cannot update flash sale item that has already started");
        }

        // Validate flash price
        if (flashSaleItemDTO.getFlashPrice() >= flashSaleItem.getProduct().getPrice()) {
            throw new RuntimeException("Flash price must be lower than original price");
        }

        flashSaleItem.setFlashPrice(flashSaleItemDTO.getFlashPrice());
        flashSaleItem.setStockLimit(flashSaleItemDTO.getStockLimit());

        FlashSaleItem updatedItem = flashSaleItemRepository.save(flashSaleItem);
        return mapFlashSaleItemToDTO(updatedItem);
    }

    @Override
    @Transactional
    public void removeProductFromFlashSale(Integer flashSaleItemId) {
        FlashSaleItem flashSaleItem = flashSaleItemRepository.findById(flashSaleItemId)
                .orElseThrow(() -> new RuntimeException("Flash sale item not found"));

        // Kiểm tra xem flash sale đã bắt đầu chưa
        LocalDateTime now = LocalDateTime.now();
        if (flashSaleItem.getFlashSale().getStartTime().isBefore(now)) {
            throw new RuntimeException("Cannot remove product from flash sale that has already started");
        }

        flashSaleItemRepository.deleteById(flashSaleItemId);
    }

    @Override
    public List<ProductDTO> getFlashSaleProducts(Integer flashSaleId) {
        List<FlashSaleItem> flashSaleItems = flashSaleItemRepository.findByFlashSaleId(flashSaleId);
        return flashSaleItems.stream()
                .map(item -> mapProductToDTO(item.getProduct(), item))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isProductInFlashSale(Integer productId) {
        LocalDateTime now = LocalDateTime.now();
        return flashSaleItemRepository.findActiveFlashSaleItemByProductId(productId, now).isPresent();
    }

    @Override
    public Optional<FlashSale> getFlashSaleEntityById(Integer id) {
        return flashSaleRepository.findById(id);
    }

    @Override
    public List<FlashSaleItemDTO> getFlashSaleItems(Integer flashSaleId) {
        List<FlashSaleItem> flashSaleItems = flashSaleItemRepository.findByFlashSaleId(flashSaleId);
        return flashSaleItems.stream()
                .map(this::mapFlashSaleItemToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FlashSaleDTO> searchFlashSalesByName(String name) {
        return flashSaleRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::mapFlashSaleToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Long countActiveFlashSales() {
        LocalDateTime now = LocalDateTime.now();
        return flashSaleRepository.countActiveFlashSales(now);
    }

    @Override
    public boolean canCreateFlashSaleInTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return !flashSaleRepository.existsActiveFlashSaleInTimeRange(startTime, endTime);
    }

    // Helper methods
    private void validateFlashSaleTime(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime now = LocalDateTime.now();

        if (startTime.isBefore(now)) {
            throw new RuntimeException("Start time cannot be in the past");
        }

        if (endTime.isBefore(startTime)) {
            throw new RuntimeException("End time must be after start time");
        }

        if (startTime.isEqual(endTime)) {
            throw new RuntimeException("Start time and end time cannot be the same");
        }
    }

    private FlashSaleDTO mapFlashSaleToDTO(FlashSale flashSale) {
        LocalDateTime now = LocalDateTime.now();

        List<FlashSaleItemDTO> items = null;
        if (flashSale.getFlashSaleItems() != null) {
            items = flashSale.getFlashSaleItems().stream()
                    .map(this::mapFlashSaleItemToDTO)
                    .collect(Collectors.toList());
        }

        return FlashSaleDTO.builder()
                .id(flashSale.getId())
                .name(flashSale.getName())
                .description(flashSale.getDescription())
                .startTime(flashSale.getStartTime())
                .endTime(flashSale.getEndTime())
                .createdAt(flashSale.getCreatedAt())
                .updatedAt(flashSale.getUpdatedAt())
                .items(items)
                .isActive(flashSale.getStartTime().isBefore(now) && flashSale.getEndTime().isAfter(now))
                .isUpcoming(flashSale.getStartTime().isAfter(now))
                .isPast(flashSale.getEndTime().isBefore(now))
                .build();
    }

    private FlashSaleItemDTO mapFlashSaleItemToDTO(FlashSaleItem flashSaleItem) {
        Product product = flashSaleItem.getProduct();
        Integer discountPercentage = calculateDiscountPercentage(product.getPrice(), flashSaleItem.getFlashPrice());
        Integer availableStock = calculateAvailableStock(flashSaleItem);

        return FlashSaleItemDTO.builder()
                .id(flashSaleItem.getId())
                .flashSaleId(flashSaleItem.getFlashSale().getId())
                .productId(product.getId())
                .productName(product.getName())
                .productImage(product.getImage())
                .originalPrice(product.getPrice())
                .flashPrice(flashSaleItem.getFlashPrice())
                .discountPercentage(discountPercentage)
                .stockLimit(flashSaleItem.getStockLimit())
                .soldCount(flashSaleItem.getSoldCount())
                .availableStock(availableStock)
                .build();
    }

    private ProductDTO mapProductToDTO(Product product, FlashSaleItem flashSaleItem) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setPrice(product.getPrice());
        productDTO.setDiscountedPrice(Math.round(flashSaleItem.getFlashPrice()));
        productDTO.setDiscountPercentage(calculateDiscountPercentage(product.getPrice(), flashSaleItem.getFlashPrice()).doubleValue());
        productDTO.setImage(product.getImage());
        productDTO.setStock(product.getStock());
        return productDTO;
    }

    private Integer calculateDiscountPercentage(Integer originalPrice, Float flashPrice) {
        if (originalPrice == null || flashPrice == null || originalPrice == 0) {
            return 0;
        }
        return Math.round(((originalPrice - flashPrice) / originalPrice) * 100);
    }

    private Integer calculateAvailableStock(FlashSaleItem flashSaleItem) {
        if (flashSaleItem.getStockLimit() == null) {
            return flashSaleItem.getProduct().getStock();
        }
        return Math.max(0, flashSaleItem.getStockLimit() - flashSaleItem.getSoldCount());
    }
}
