package com.example.electronics_store.service.impl;

import com.example.electronics_store.dto.FlashSaleDTO;
import com.example.electronics_store.dto.FlashSaleItemDTO;
import com.example.electronics_store.dto.ProductDTO;
import com.example.electronics_store.model.FlashSale;
import com.example.electronics_store.model.FlashSaleItem;
import com.example.electronics_store.model.Product;
import com.example.electronics_store.repository.FlashSaleItemRepository;
import com.example.electronics_store.repository.FlashSaleRepository;
import com.example.electronics_store.repository.ProductRepository;
import com.example.electronics_store.service.FlashSaleService;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FlashSaleServiceImpl implements FlashSaleService {

    @Autowired private FlashSaleRepository flashSaleRepository;
    @Autowired private FlashSaleItemRepository flashSaleItemRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CacheManager cacheManager;

    /* -------------------- Helpers for cache -------------------- */

    private void evictProductsByIds(Collection<Integer> ids) {
        if (ids == null || ids.isEmpty()) return;
        var products = cacheManager.getCache("products");
        if (products != null) {
            ids.forEach(products::evict);
        }
    }


    /* -------------------- CRUD flash sale -------------------- */

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "flashSales", allEntries = true),
        @CacheEvict(value = "productLists", allEntries = true)
    })
    public FlashSaleDTO createFlashSale(FlashSaleDTO flashSaleDTO) {
        validateFlashSaleTime(flashSaleDTO.getStartTime(), flashSaleDTO.getEndTime());

        if (flashSaleRepository.existsActiveFlashSaleInTimeRange(
                flashSaleDTO.getStartTime(), flashSaleDTO.getEndTime())) {
            throw new RuntimeException("There is already an active flash sale in this time range");
        }

        FlashSale flashSale = new FlashSale();
        flashSale.setName(flashSaleDTO.getName());
        flashSale.setDescription(flashSaleDTO.getDescription());
        flashSale.setStartTime(flashSaleDTO.getStartTime());
        flashSale.setEndTime(flashSaleDTO.getEndTime());

        FlashSale saved = flashSaleRepository.save(flashSale);
        return mapFlashSaleToDTO(saved);
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "flashSales", allEntries = true),
        @CacheEvict(value = "productLists", allEntries = true)
    })
    public FlashSaleDTO updateFlashSale(Integer id, FlashSaleDTO flashSaleDTO) {
        FlashSale flashSale = flashSaleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flash sale not found"));

        LocalDateTime now = LocalDateTime.now();
        if (flashSale.getStartTime().isBefore(now)) {
            throw new RuntimeException("Cannot update flash sale that has already started");
        }

        LocalDateTime newStart = flashSaleDTO.getStartTime();
        LocalDateTime newEnd = flashSaleDTO.getEndTime();

        if (newStart == null && newEnd != null) {
            newStart = flashSale.getStartTime();
            if (newEnd.isBefore(newStart)) throw new RuntimeException("End time must be after start time");
            if (!flashSale.getEndTime().equals(newEnd)
                    && flashSaleRepository.existsActiveFlashSaleInTimeRange(newStart, newEnd)) {
                throw new IllegalArgumentException("There is already another flash sale in this time range");
            }
            flashSale.setEndTime(newEnd);
        } else if (newStart != null && newEnd == null) {
            newEnd = flashSale.getEndTime();
            if (newStart.isAfter(newEnd)) throw new RuntimeException("Start time must be before end time");
            if (newStart.isBefore(now)) throw new RuntimeException("Start time cannot be in the past");
            if (!flashSale.getStartTime().equals(newStart)
                    && flashSaleRepository.existsActiveFlashSaleInTimeRange(newStart, newEnd)) {
                throw new IllegalArgumentException("There is already another flash sale in this time range");
            }
            flashSale.setStartTime(newStart);
        } else if (newStart != null && newEnd != null) {
            validateFlashSaleTime(newStart, newEnd);
            if ((!flashSale.getStartTime().equals(newStart) || !flashSale.getEndTime().equals(newEnd))
                    && flashSaleRepository.existsActiveFlashSaleInTimeRange(newStart, newEnd)) {
                throw new IllegalArgumentException("There is already another flash sale in this time range");
            }
            flashSale.setStartTime(newStart);
            flashSale.setEndTime(newEnd);
        }

        if (flashSaleDTO.getName() != null) flashSale.setName(flashSaleDTO.getName());
        if (flashSaleDTO.getDescription() != null) flashSale.setDescription(flashSaleDTO.getDescription());

        FlashSale updated = flashSaleRepository.save(flashSale);

        // Evict products under this flash sale (giá “hiện tại” phụ thuộc vào khung giờ)
        List<Integer> affectedProductIds = flashSaleItemRepository.findByFlashSaleId(id)
                .stream().map(i -> i.getProduct().getId()).collect(Collectors.toList());
        evictProductsByIds(affectedProductIds);
        // flashSales + productLists đã clear bởi @Caching

        return mapFlashSaleToDTO(updated);
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "flashSales", allEntries = true),
        @CacheEvict(value = "productLists", allEntries = true)
    })
    public void deleteFlashSale(Integer id) {
        FlashSale flashSale = flashSaleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flash sale not found"));

        LocalDateTime now = LocalDateTime.now();
        if (flashSale.getStartTime().isBefore(now)) {
            throw new RuntimeException("Cannot delete flash sale that has already started");
        }

        // Evict sản phẩm trước khi xóa
        List<Integer> productIds = flashSaleItemRepository.findByFlashSaleId(id)
                .stream().map(i -> i.getProduct().getId()).collect(Collectors.toList());
        evictProductsByIds(productIds);

        flashSaleRepository.deleteById(id);
        // flashSales + productLists đã clear bởi @Caching
    }

    /* -------------------- Items (product in flash sale) -------------------- */

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "flashSales", allEntries = true),
        @CacheEvict(value = "productLists", allEntries = true)
    })
    public FlashSaleItemDTO addProductToFlashSale(Integer flashSaleId, FlashSaleItemDTO dto) {
        FlashSale flashSale = flashSaleRepository.findById(flashSaleId)
                .orElseThrow(() -> new RuntimeException("Flash sale not found"));
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        boolean exists = flashSaleItemRepository.findByFlashSaleId(flashSaleId).stream()
                .anyMatch(item -> item.getProduct().getId().equals(dto.getProductId()));
        if (exists) throw new RuntimeException("Product already exists in this flash sale");

        if (dto.getFlashPrice() == null || dto.getFlashPrice() >= product.getPrice()) {
            throw new RuntimeException("Flash price must be lower than original price");
        }

        FlashSaleItem item = new FlashSaleItem();
        item.setFlashSale(flashSale);
        item.setProduct(product);
        item.setFlashPrice(dto.getFlashPrice());
        item.setStockLimit(dto.getStockLimit());
        item.setSoldCount(0);

        FlashSaleItem saved = flashSaleItemRepository.save(item);

        // Evict đúng sản phẩm
        evictProductsByIds(List.of(product.getId()));

        return mapFlashSaleItemToDTO(saved);
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "flashSales", allEntries = true),
        @CacheEvict(value = "productLists", allEntries = true)
    })
    public FlashSaleItemDTO updateFlashSaleItem(Integer flashSaleItemId, FlashSaleItemDTO dto) {
        FlashSaleItem item = flashSaleItemRepository.findById(flashSaleItemId)
                .orElseThrow(() -> new RuntimeException("Flash sale item not found"));

        LocalDateTime now = LocalDateTime.now();
        if (item.getFlashSale().getStartTime().isBefore(now)) {
            throw new RuntimeException("Cannot update flash sale item that has already started");
        }

        if (dto.getFlashPrice() == null || dto.getFlashPrice() >= item.getProduct().getPrice()) {
            throw new RuntimeException("Flash price must be lower than original price");
        }

        item.setFlashPrice(dto.getFlashPrice());
        item.setStockLimit(dto.getStockLimit());
        FlashSaleItem updated = flashSaleItemRepository.save(item);

        // Evict đúng sản phẩm
        evictProductsByIds(List.of(item.getProduct().getId()));

        return mapFlashSaleItemToDTO(updated);
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "flashSales", allEntries = true),
        @CacheEvict(value = "productLists", allEntries = true)
    })
    public void removeProductFromFlashSale(Integer flashSaleItemId) {
        FlashSaleItem item = flashSaleItemRepository.findById(flashSaleItemId)
                .orElseThrow(() -> new RuntimeException("Flash sale item not found"));

        LocalDateTime now = LocalDateTime.now();
        if (item.getFlashSale().getStartTime().isBefore(now)) {
            throw new RuntimeException("Cannot remove product from flash sale that has already started");
        }

        Integer productId = item.getProduct().getId();
        flashSaleItemRepository.deleteById(flashSaleItemId);

        // Evict đúng sản phẩm
        evictProductsByIds(List.of(productId));
    }

    /* -------------------- Reads with caching -------------------- */

    @Override
    @Transactional(readOnly = true)
    
    public Page<ProductDTO> getFlashSaleProductsWithFilters(Integer flashSaleId, String search, Integer minPrice, Integer maxPrice, Pageable pageable) {
        // Validate & build spec
        flashSaleRepository.findById(flashSaleId)
                .orElseThrow(() -> new RuntimeException("Flash sale not found"));

        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<Product, FlashSaleItem> fsItem = root.join("flashSaleItems", JoinType.INNER);
            Join<FlashSaleItem, FlashSale> fs = fsItem.join("flashSale", JoinType.INNER);

            predicates.add(cb.equal(fs.get("id"), flashSaleId));

            if (search != null && !search.trim().isEmpty()) {
                String term = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), term),
                        cb.like(cb.lower(root.get("description")), term)
                ));
            }

            if (minPrice != null) predicates.add(cb.greaterThanOrEqualTo(fsItem.get("flashPrice"), minPrice));
            if (maxPrice != null) predicates.add(cb.lessThanOrEqualTo(fsItem.get("flashPrice"), maxPrice));

            predicates.add(cb.isTrue(root.get("status")));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Product> page = productRepository.findAll(spec, pageable);

        return page.map(p -> {
            FlashSaleItem fsi = flashSaleItemRepository.findByFlashSaleIdAndProductId(flashSaleId, p.getId()).orElse(null);
            return mapProductToDTO(p, fsi);
        });
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "flashSales", key = "#id")
    public FlashSaleDTO getFlashSaleById(Integer id) {
        FlashSale flashSale = flashSaleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flash sale not found"));
        return mapFlashSaleToDTO(flashSale);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "flashSales", key = "'all'")
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
            String term = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                try {
                    Integer id = Integer.parseInt(search);
                    predicates.add(cb.equal(root.get("id"), id));
                } catch (NumberFormatException ignored) { }

                predicates.add(cb.like(cb.lower(root.get("name")), term));
                predicates.add(cb.like(cb.lower(root.get("description")), term));

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

                if (search.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    predicates.add(cb.like(cb.function("DATE_FORMAT", String.class, root.get("startTime"), cb.literal("%Y-%m-%d")), search));
                    predicates.add(cb.like(cb.function("DATE_FORMAT", String.class, root.get("endTime"), cb.literal("%Y-%m-%d")), search));
                }

                return cb.or(predicates.toArray(new Predicate[0]));
            });
        }

        Page<FlashSale> page = flashSaleRepository.findAll(spec, pageable);
        return page.map(this::mapFlashSaleToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "flashSales", key = "'current'")
    public List<FlashSaleDTO> getCurrentFlashSales() {
        LocalDateTime now = LocalDateTime.now();
        return flashSaleRepository.findCurrentFlashSales(now).stream()
                .map(this::mapFlashSaleToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "flashSales", key = "'upcoming'")
    public List<FlashSaleDTO> getUpcomingFlashSales() {
        LocalDateTime now = LocalDateTime.now();
        return flashSaleRepository.findUpcomingFlashSales(now).stream()
                .map(this::mapFlashSaleToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "flashSales", key = "'past'")
    public List<FlashSaleDTO> getPastFlashSales() {
        LocalDateTime now = LocalDateTime.now();
        return flashSaleRepository.findPastFlashSales(now).stream()
                .map(this::mapFlashSaleToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getFlashSaleProducts(Integer flashSaleId) {
        List<FlashSaleItem> items = flashSaleItemRepository.findByFlashSaleId(flashSaleId);
        return items.stream()
                .map(i -> mapProductToDTO(i.getProduct(), i))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProductInFlashSale(Integer productId) {
        LocalDateTime now = LocalDateTime.now();
        return flashSaleItemRepository.findActiveFlashSaleItemByProductId(productId, now).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "flashSales", key = "'items:' + #flashSaleId")
    public List<FlashSaleItemDTO> getFlashSaleItems(Integer flashSaleId) {
        List<FlashSaleItem> items = flashSaleItemRepository.findByFlashSaleId(flashSaleId);
        return items.stream().map(this::mapFlashSaleItemToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FlashSale> getFlashSaleEntityById(Integer id) {
        return flashSaleRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashSaleDTO> searchFlashSalesByName(String name) {
        return flashSaleRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::mapFlashSaleToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Long countActiveFlashSales() {
        LocalDateTime now = LocalDateTime.now();
        return flashSaleRepository.countActiveFlashSales(now);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canCreateFlashSaleInTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return !flashSaleRepository.existsActiveFlashSaleInTimeRange(startTime, endTime);
    }

    /* -------------------- Private mappers & validators -------------------- */

    private void validateFlashSaleTime(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime now = LocalDateTime.now();
        if (startTime.isBefore(now)) throw new RuntimeException("Start time cannot be in the past");
        if (endTime.isBefore(startTime)) throw new RuntimeException("End time must be after start time");
        if (startTime.isEqual(endTime)) throw new RuntimeException("Start time and end time cannot be the same");
    }

    private FlashSaleDTO mapFlashSaleToDTO(FlashSale flashSale) {
        LocalDateTime now = LocalDateTime.now();
        List<FlashSaleItemDTO> items = (flashSale.getFlashSaleItems() == null) ? null :
                flashSale.getFlashSaleItems().stream().map(this::mapFlashSaleItemToDTO).collect(Collectors.toList());

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
        if (originalPrice == null || flashPrice == null || originalPrice == 0) return 0;
        return Math.round(((originalPrice - flashPrice) / originalPrice) * 100);
    }

    private Integer calculateAvailableStock(FlashSaleItem flashSaleItem) {
        if (flashSaleItem.getStockLimit() == null) return flashSaleItem.getProduct().getStock();
        return Math.max(0, flashSaleItem.getStockLimit() - flashSaleItem.getSoldCount());
    }
}
