package com.example.electronics_store.service.impl;

import com.example.electronics_store.dto.OrderCreateDTO;
import com.example.electronics_store.dto.OrderDTO;
import com.example.electronics_store.dto.OrderDetailDTO;
import com.example.electronics_store.model.*;
import com.example.electronics_store.repository.*;
import com.example.electronics_store.service.CartService;
import com.example.electronics_store.service.DiscountService;
import com.example.electronics_store.service.OrderService;
import com.example.electronics_store.service.ProductSalesService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ShippingMethodRepository shippingMethodRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final FlashSaleItemRepository flashSaleItemRepository;
    private final ProductSalesService productSalesService;
    private final DiscountService discountService;
    private final EntityManager entityManager;

    @Autowired
    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderDetailRepository orderDetailRepository,
            UserRepository userRepository,
            CartService cartService,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            ShippingMethodRepository shippingMethodRepository,
            PaymentMethodRepository paymentMethodRepository,
            DiscountService discountService, FlashSaleItemRepository flashSaleItemRepository, ProductSalesService productSalesService, EntityManager entityManager) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.shippingMethodRepository = shippingMethodRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.flashSaleItemRepository = flashSaleItemRepository;
        this.productSalesService = productSalesService;
        this.discountService = discountService;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public OrderDTO createOrder(Integer userId, OrderCreateDTO orderCreateDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart is empty"));

        // Lấy các sản phẩm đã được chọn
        List<CartItem> selectedCartItems = cartItemRepository.findByCartAndSelected(cart, true);
        if (selectedCartItems.isEmpty()) {
            throw new RuntimeException("No items selected for checkout");
        }

        // Check if all products are in stock and validate flash sale items
        LocalDateTime orderTime = LocalDateTime.now();
        for (CartItem cartItem : selectedCartItems) {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }

            // Kiểm tra flash sale stock limit
            Optional<FlashSaleItem> flashSaleItem = flashSaleItemRepository.findActiveFlashSaleItemByProductId(
                    product.getId(), orderTime);
            if (flashSaleItem.isPresent()) {
                FlashSaleItem item = flashSaleItem.get();
                if (item.getStockLimit() != null) {
                    int availableFlashSaleStock = item.getStockLimit() - item.getSoldCount();
                    if (availableFlashSaleStock < cartItem.getQuantity()) {
                        throw new RuntimeException("Not enough flash sale stock for product: " + product.getName());
                    }
                }
            }
        }

        // Get shipping method
        ShippingMethod shippingMethod = shippingMethodRepository.findById(orderCreateDTO.getShippingMethodId())
                .orElseThrow(() -> new RuntimeException("Shipping method not found"));

        // Get payment method
        PaymentMethod paymentMethod = paymentMethodRepository.findById(orderCreateDTO.getPaymentMethodId())
                .orElseThrow(() -> new RuntimeException("Payment method not found"));

        Float totalPrice = 0f;

        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setShippingFee(shippingMethod.getBaseCost());
        order.setAddress(orderCreateDTO.getAddress());
        order.setPhoneNumber(orderCreateDTO.getPhoneNumber());
        order.setShippingMethod(shippingMethod);
        order.setPaymentMethod(paymentMethod);
        order.setPaymentStatus("Pending");
        order.setOrderStatus(0); // 0: Pending, 1: Processing, 2: Shipped, 3: Delivered, 4: Completed, 5: Cancelled

        // Create order details
        List<OrderDetail> orderDetails = new ArrayList<>();
        List<Integer> cartItemIds = new ArrayList<>();
        for (CartItem cartItem : selectedCartItems) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(cartItem.getProduct());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setReviewStatus(false);
            Float currentPrice = calculateDiscountedPriceAtTime(cartItem.getProduct(), orderTime);
            orderDetail.setPrice(currentPrice);
            totalPrice += currentPrice * cartItem.getQuantity();
            orderDetails.add(orderDetail);

            // Update product stock
            Product product = cartItem.getProduct();
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            // Update flash sale sold count if product is in flash sale
            Optional<FlashSaleItem> flashSaleItem = flashSaleItemRepository.findActiveFlashSaleItemByProductId(
                    product.getId(), orderTime);
            if (flashSaleItem.isPresent()) {
                flashSaleItemRepository.updateSoldCount(flashSaleItem.get().getId(), cartItem.getQuantity());
            }

            cartItemIds.add(cartItem.getId());
        }
        order.setTotalPrice(totalPrice);
        orderDetailRepository.saveAll(orderDetails);
        Order savedOrder = orderRepository.save(order);
        cartItemRepository.deleteAllByIds(cartItemIds);

        entityManager.flush(); // đẩy các thay đổi cho orderdetail trước khi map
       
        return mapOrderToDTO(savedOrder);
    }

    @Override
    public OrderDTO getOrderById(Integer id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return mapOrderToDTO(order);
    }

    @Override
    public List<OrderDTO> getOrdersByUserId(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepository.findByUser(user).stream()
                .map(this::mapOrderToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapOrderToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getOrdersByStatus(Integer status) {
        return orderRepository.findByOrderStatus(status).stream()
                .map(this::mapOrderToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getOrdersByPaymentStatus(String paymentStatus) {
        return orderRepository.findByPaymentStatus(paymentStatus).stream()
                .map(this::mapOrderToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> getOrdersWithSearch(String search, Pageable pageable) {
        Specification<Order> spec = Specification.where(null);
        if (search != null && !search.trim().isEmpty()) {
            Integer orderId = null;
            try {
                orderId = Integer.parseInt(search);
            } catch (NumberFormatException ignored) {
            }
            Integer orderStatus = null;
            if ("pending".equalsIgnoreCase(search)) orderStatus = 0;
            else if ("processing".equalsIgnoreCase(search)) orderStatus = 1;
            else if ("shipped".equalsIgnoreCase(search)) orderStatus = 2;
            else if ("delivered".equalsIgnoreCase(search)) orderStatus = 3;
            else if ("completed".equalsIgnoreCase(search)) orderStatus = 4;
            else if ("cancelled".equalsIgnoreCase(search)) orderStatus = 5;
            final Integer finalOrderId = orderId;
            final Integer finalOrderStatus = orderStatus;

            // only search by ID
            if (finalOrderId != null) {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("id"), finalOrderId));
            }
            // only search by status
            else if (finalOrderStatus != null) {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("orderStatus"), finalOrderStatus));
            }
            else {
                String searchTerm = "%" + search.toLowerCase() + "%";
                spec = spec.and((root, query, cb) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(cb.like(cb.lower(root.get("id").as(String.class)), searchTerm));
                    predicates.add(cb.like(cb.lower(root.get("totalPrice").as(String.class)), searchTerm));
                    predicates.add(cb.like(cb.lower(root.get("shippingFee").as(String.class)), searchTerm));
                    predicates.add(cb.like(cb.lower(root.get("address")), searchTerm));
                    predicates.add(cb.like(cb.lower(root.get("phoneNumber")), searchTerm));
                    predicates.add(cb.like(cb.lower(root.get("trackingNumber")), searchTerm));
                    if (search.matches("\\d{4}-\\d{2}(-\\d{2})?")) {
                        predicates.add(cb.like(cb.function("DATE_FORMAT", String.class, root.get("createAt"), cb.literal("%Y-%m-%d")), search + "%"));
                    }
                    predicates.add(cb.like(cb.lower(root.get("paymentStatus")), searchTerm));
                    predicates.add(cb.like(cb.lower(root.get("orderStatus").as(String.class)), searchTerm));

                    // Search in related user fields
                    predicates.add(cb.like(cb.lower(root.get("user").get("userName")), searchTerm));
                    predicates.add(cb.like(cb.lower(root.get("user").get("email")), searchTerm));
                    predicates.add(cb.like(cb.lower(root.get("user").get("surName")), searchTerm));
                    predicates.add(cb.like(cb.lower(root.get("user").get("lastName")), searchTerm));

                    // Search in related shipping method
                    predicates.add(cb.like(cb.lower(root.get("shippingMethod").get("methodName")), searchTerm));

                    // Search in related payment method
                    predicates.add(cb.like(cb.lower(root.get("paymentMethod").get("methodName")), searchTerm));

                    return cb.or(predicates.toArray(new Predicate[0]));
                });
            }
        }


        Page<Order> orderPage = orderRepository.findAll(spec, pageable);

        // Map to DTOs
        return orderPage.map(this::mapOrderToDTO);
    }
    @Override
    public List<OrderDTO> getOrdersInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findOrdersInDateRange(startDate, endDate).stream()
                .map(this::mapOrderToDTO)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> getAllOrdersWithPagination(Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAll(pageable);
        return orderPage.map(this::mapOrderToDTO);
    }
    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Integer id, Integer status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Validate status
        if (status < 0 || status > 5) {
            throw new RuntimeException("Invalid order status");
        }

        // If cancelling order, restore product stock and decrease sold quantity
        if (status == 5 && order.getOrderStatus() != 5) {
            List<OrderDetail> orderDetails = orderDetailRepository.findByOrder(order);
            for (OrderDetail orderDetail : orderDetails) {
                Product product = orderDetail.getProduct();
                product.setStock(product.getStock() + orderDetail.getQuantity());
                productRepository.save(product);

                // Decrease sold quantity if it was previously completed
                if (order.getOrderStatus() == 4) {
                    productSalesService.updateSoldQuantity(product.getId(), -orderDetail.getQuantity());
                }
            }
        }

        // If completing order, update sold quantity
        if (status == 4 && order.getOrderStatus() != 4) {
            List<OrderDetail> orderDetails = orderDetailRepository.findByOrder(order);
            for (OrderDetail orderDetail : orderDetails) {
                productSalesService.updateSoldQuantity(orderDetail.getProduct().getId(), orderDetail.getQuantity());
            }
        }

        order.setOrderStatus(status);
        Order updatedOrder = orderRepository.save(order);

        return mapOrderToDTO(updatedOrder);
    }

    @Override
    @Transactional
    public OrderDTO updatePaymentStatus(Integer id, String paymentStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Validate payment status
        if (!Arrays.asList("Pending", "Paid", "Failed", "Refunded").contains(paymentStatus)) {
            throw new RuntimeException("Invalid payment status");
        }

        order.setPaymentStatus(paymentStatus);
        Order updatedOrder = orderRepository.save(order);

        return mapOrderToDTO(updatedOrder);
    }

    @Override
    @Transactional
    public OrderDTO updateTrackingNumber(Integer id, String trackingNumber) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setTrackingNumber(trackingNumber);
        Order updatedOrder = orderRepository.save(order);

        return mapOrderToDTO(updatedOrder);
    }

    @Override
    @Transactional
    public void deleteOrder(Integer id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order not found");
        }
        orderRepository.deleteById(id);
    }

    @Override
    public Float calculateTotalSalesInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        Float total = orderRepository.calculateTotalSalesInDateRange(startDate, endDate);
        return total != null ? total : 0f;
    }

    @Override
    public Long countOrdersByUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepository.countOrdersByUser(user);
    }

    @Override
    public Map<String, Object> getOrderStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startDateStr = startDate.format(formatter);
        String endDateStr = endDate.format(formatter);

        List<Object[]> orderCountByDay = orderRepository.getOrderCountByDay(startDateStr, endDateStr);
        List<Object[]> salesByDay = orderRepository.getSalesByDay(startDateStr, endDateStr);

        Map<String, Long> orderCountMap = new LinkedHashMap<>();
        for (Object[] row : orderCountByDay) {
            String date = row[0].toString();
            Long count = ((Number) row[1]).longValue();
            orderCountMap.put(date, count);
        }

        Map<String, Float> salesMap = new LinkedHashMap<>();
        for (Object[] row : salesByDay) {
            String date = row[0].toString();
            Float sales = ((Number) row[1]).floatValue();
            salesMap.put(date, sales);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("orderCountByDay", orderCountMap);
        result.put("salesByDay", salesMap);

        return result;
    }

    @Override
    public Optional<Order> getOrderEntityById(Integer id) {
        return orderRepository.findById(id);
    }

    // Helper methods
    private OrderDTO mapOrderToDTO(Order order) {
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrder(order);
        List<OrderDetailDTO> orderDetailDTOs = orderDetails.stream()
                .map(this::mapOrderDetailToDTO)
                .collect(Collectors.toList());

        String orderStatusText;
        switch (order.getOrderStatus()) {
            case 0:
                orderStatusText = "Pending";
                break;
            case 1:
                orderStatusText = "Processing";
                break;
            case 2:
                orderStatusText = "Shipped";
                break;
            case 3:
                orderStatusText = "Delivered";
                break;
            case 4:
                orderStatusText = "Completed";
                break;
            case 5:
                orderStatusText = "Cancelled";
                break;
            default:
                orderStatusText = "Unknown";
        }

        return OrderDTO.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .userName(order.getUser().getUserName())
                .totalPrice(order.getTotalPrice())
                .shippingFee(order.getShippingFee())
                .address(order.getAddress())
                .phoneNumber(order.getPhoneNumber())
                .shippingMethodId(order.getShippingMethod() != null ? order.getShippingMethod().getId() : null)
                .shippingMethodName(order.getShippingMethod() != null ? order.getShippingMethod().getMethodName() : null)
                .paymentMethodId(order.getPaymentMethod() != null ? order.getPaymentMethod().getId() : null)
                .paymentMethodName(order.getPaymentMethod() != null ? order.getPaymentMethod().getMethodName() : null)
                .trackingNumber(order.getTrackingNumber())
                .createAt(order.getCreateAt())
                .paymentStatus(order.getPaymentStatus())
                .orderStatus(order.getOrderStatus())
                .orderStatusText(orderStatusText)
                .orderDetails(orderDetailDTOs)
                .build();
    }

    private OrderDetailDTO mapOrderDetailToDTO(OrderDetail orderDetail) {
        Float price = orderDetail.getPrice();
        Float totalPrice = price * orderDetail.getQuantity();

        return OrderDetailDTO.builder()
                .id(orderDetail.getId())
                .orderId(orderDetail.getOrder().getId())
                .productId(orderDetail.getProduct().getId())
                .productName(orderDetail.getProduct().getName())
                .productImage(orderDetail.getProduct().getImage())
                .quantity(orderDetail.getQuantity())
                .price(price)
                .totalPrice(totalPrice)
                .reviewStatus(orderDetail.getReviewStatus())
                .build();
    }
    private Float calculateDiscountedPriceAtTime(Product product, LocalDateTime orderTime) {
       return discountService.calculateProductPriceAtTime(product.getId(), orderTime);
    }
}
