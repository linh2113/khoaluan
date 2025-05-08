package com.example.electronics_store.service.impl;

import com.example.electronics_store.dto.DiscountDTO;
import com.example.electronics_store.model.Discount;
import com.example.electronics_store.repository.DiscountRepository;
import com.example.electronics_store.service.DiscountService;
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
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;

    @Autowired
    public DiscountServiceImpl(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    @Override
    @Transactional
    public DiscountDTO createDiscount(DiscountDTO discountDTO) {
        // Check if code already exists
        if (discountRepository.findByCode(discountDTO.getCode()).isPresent()) {
            throw new RuntimeException("Discount code already exists");
        }

        Discount discount = new Discount();
        discount.setCode(discountDTO.getCode());
        discount.setDiscountName(discountDTO.getDiscountName());
        discount.setDescription(discountDTO.getDescription());
        discount.setValue(discountDTO.getValue());
        discount.setQuantity(discountDTO.getQuantity());
        discount.setStartDate(discountDTO.getStartDate());
        discount.setEndDate(discountDTO.getEndDate());

        Discount savedDiscount = discountRepository.save(discount);
        return mapDiscountToDTO(savedDiscount);
    }

    @Override
    @Transactional
    public DiscountDTO updateDiscount(Integer id, DiscountDTO discountDTO) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Discount not found"));

        // Check if code already exists for another discount
        if (discountDTO.getCode() != null) {
            Optional<Discount> existingDiscount = discountRepository.findByCode(discountDTO.getCode());
            if (existingDiscount.isPresent() && !existingDiscount.get().getId().equals(id)) {
                throw new RuntimeException("Discount code already exists");
            }
            discount.setCode(discountDTO.getCode());
        }

        if (discountDTO.getDiscountName() != null) {
            discount.setDiscountName(discountDTO.getDiscountName());
        }
        if (discountDTO.getDescription() != null) {
            discount.setDescription(discountDTO.getDescription());
        }
        if (discountDTO.getValue() != null) {
            discount.setValue(discountDTO.getValue());
        }
        if (discountDTO.getQuantity() != null) {
            discount.setQuantity(discountDTO.getQuantity());
        }
        if (discountDTO.getStartDate() != null) {
            discount.setStartDate(discountDTO.getStartDate());
        }
        if (discountDTO.getEndDate() != null) {
            discount.setEndDate(discountDTO.getEndDate());
        }

        Discount updatedDiscount = discountRepository.save(discount);
        return mapDiscountToDTO(updatedDiscount);
    }

    @Override
    public DiscountDTO getDiscountById(Integer id) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Discount not found"));
        return mapDiscountToDTO(discount);
    }

    @Override
    public DiscountDTO getDiscountByCode(String code) {
        Discount discount = discountRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Discount not found"));
        return mapDiscountToDTO(discount);
    }

    @Override
    public List<DiscountDTO> getAllDiscounts() {
        return discountRepository.findAll().stream()
                .map(this::mapDiscountToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DiscountDTO> getAllActiveDiscounts() {
        LocalDateTime now = LocalDateTime.now();
        return discountRepository.findAllActiveDiscounts(now).stream()
                .map(this::mapDiscountToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DiscountDTO> getAllExpiredDiscounts() {
        LocalDateTime now = LocalDateTime.now();
        return discountRepository.findAllExpiredDiscounts(now).stream()
                .map(this::mapDiscountToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DiscountDTO> getAllUpcomingDiscounts() {
        LocalDateTime now = LocalDateTime.now();
        return discountRepository.findAllUpcomingDiscounts(now).stream()
                .map(this::mapDiscountToDTO)
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

        return discount.getStartDate().isBefore(now) &&
                discount.getEndDate().isAfter(now) &&
                discount.getQuantity() > 0;
    }

    @Override
    public Float applyDiscount(Integer discountId, Float amount) {
        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new RuntimeException("Discount not found"));

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(discount.getStartDate()) || now.isAfter(discount.getEndDate())) {
            throw new RuntimeException("Discount is not active");
        }

        if (discount.getQuantity() <= 0) {
            throw new RuntimeException("Discount is out of stock");
        }

        // Apply discount
        double discountValue = discount.getValue();
        return (float) (amount * (1 - discountValue / 100));
    }

    @Override
    @Transactional
    public void useDiscount(Integer discountId) {
        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new RuntimeException("Discount not found"));

        if (discount.getQuantity() <= 0) {
            throw new RuntimeException("Discount is out of stock");
        }

        discount.setQuantity(discount.getQuantity() - 1);
        discountRepository.save(discount);
    }

    @Override
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
                predicates.add(cb.like(cb.lower(root.get("description")), searchTerm));
                predicates.add(cb.like(cb.lower(root.get("discountName")), searchTerm));
                predicates.add(cb.like(cb.lower(root.get("value").as(String.class)), searchTerm));
                predicates.add(cb.like(cb.lower(root.get("quantity").as(String.class)), searchTerm));
                predicates.add(cb.like(cb.lower(root.get("startDate").as(String.class)), searchTerm));
                predicates.add(cb.like(cb.lower(root.get("endDate").as(String.class)), searchTerm));

                // Thêm tìm kiếm theo trạng thái
                if ("active".equalsIgnoreCase(search)) {
                    LocalDateTime now = LocalDateTime.now();
                    predicates.add(cb.and(
                            cb.lessThanOrEqualTo(root.get("startDate"), now),
                            cb.greaterThanOrEqualTo(root.get("endDate"), now),
                            cb.greaterThan(root.get("quantity"), 0)
                    ));
                } else if ("expired".equalsIgnoreCase(search)) {
                    predicates.add(cb.lessThan(root.get("endDate"), LocalDateTime.now()));
                } else if ("upcoming".equalsIgnoreCase(search)) {
                    predicates.add(cb.greaterThan(root.get("startDate"), LocalDateTime.now()));
                } else if ("out_of_stock".equalsIgnoreCase(search) || "outofstock".equalsIgnoreCase(search)) {
                    predicates.add(cb.equal(root.get("quantity"), 0));
                }

                return cb.or(predicates.toArray(new Predicate[0]));
            });
        }

        Page<Discount> discountPage = discountRepository.findAll(spec, pageable);
        return discountPage.map(this::mapDiscountToDTO);
    }

    @Override
    public Optional<Discount> getDiscountEntityById(Integer id) {
        return discountRepository.findById(id);
    }

    @Override
    public Optional<Discount> getDiscountEntityByCode(String code) {
        return discountRepository.findByCode(code);
    }

    // Helper method
    private DiscountDTO mapDiscountToDTO(Discount discount) {
        LocalDateTime now = LocalDateTime.now();
        boolean isActive = discount.getStartDate().isBefore(now) &&
                discount.getEndDate().isAfter(now) &&
                discount.getQuantity() > 0;

        return DiscountDTO.builder()
                .id(discount.getId())
                .code(discount.getCode())
                .discountName(discount.getDiscountName())
                .description(discount.getDescription())
                .value(discount.getValue())
                .quantity(discount.getQuantity())
                .startDate(discount.getStartDate())
                .endDate(discount.getEndDate())
                .isActive(isActive)
                .build();
    }
}
