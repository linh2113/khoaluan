package com.example.electronics_store.service.impl;

import com.example.electronics_store.dto.OrderCreateDTO;
import com.example.electronics_store.dto.OrderDTO;
import com.example.electronics_store.dto.OrderDetailDTO;
import com.example.electronics_store.model.*;
import com.example.electronics_store.repository.*;
import com.example.electronics_store.service.CartService;
import com.example.electronics_store.service.DiscountService;
import com.example.electronics_store.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final CartService cartService;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ShippingMethodRepository shippingMethodRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final DiscountService discountService;
    
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
            DiscountService discountService) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.userRepository = userRepository;
        this.cartService = cartService;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.shippingMethodRepository = shippingMethodRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.discountService = discountService;
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

        // Check if all products are in stock
        for (CartItem cartItem : selectedCartItems) {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }
        }

        // Get shipping method
        ShippingMethod shippingMethod = shippingMethodRepository.findById(orderCreateDTO.getShippingMethodId())
                .orElseThrow(() -> new RuntimeException("Shipping method not found"));

        // Get payment method
        PaymentMethod paymentMethod = paymentMethodRepository.findById(orderCreateDTO.getPaymentMethodId())
                .orElseThrow(() -> new RuntimeException("Payment method not found"));

        // Calculate total price
        Float totalPrice = selectedCartItems.stream()
                .map(item -> item.getPrice() * item.getQuantity())
                .reduce(0f, Float::sum);

        // Apply discount if provided
        if (orderCreateDTO.getDiscountCode() != null && !orderCreateDTO.getDiscountCode().isEmpty()) {
            if (discountService.isDiscountValid(orderCreateDTO.getDiscountCode())) {
                totalPrice = discountService.applyDiscount(orderCreateDTO.getDiscountCode(), totalPrice);
                // Mark discount as used
                discountService.useDiscount(orderCreateDTO.getDiscountCode());
            } else {
                throw new RuntimeException("Invalid or expired discount code");
            }
        }

        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setTotalPrice(totalPrice);
        order.setShippingFee(shippingMethod.getBaseCost());
        order.setAddress(orderCreateDTO.getAddress());
        order.setPhoneNumber(orderCreateDTO.getPhoneNumber());
        order.setShippingMethod(shippingMethod);
        order.setPaymentMethod(paymentMethod);
        order.setPaymentStatus("Pending");
        order.setOrderStatus(0); // 0: Pending, 1: Processing, 2: Shipped, 3: Delivered, 4: Completed, 5: Cancelled

        Order savedOrder = orderRepository.save(order);

        // Create order details
        List<OrderDetail> orderDetails = new ArrayList<>();
        List<Integer> cartItemIds = new ArrayList<>();
        for (CartItem cartItem : selectedCartItems) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(savedOrder);
            orderDetail.setProduct(cartItem.getProduct());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setReviewStatus(false);

            orderDetails.add(orderDetail);

            // Update product stock
            Product product = cartItem.getProduct();
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            cartItemIds.add(cartItem.getId());
        }
        orderDetailRepository.saveAll(orderDetails);
        cartItemRepository.deleteAllByIds(cartItemIds);
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
    public List<OrderDTO> getOrdersInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findOrdersInDateRange(startDate, endDate).stream()
                .map(this::mapOrderToDTO)
                .collect(Collectors.toList());
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
        
        // If cancelling order, restore product stock
        if (status == 5 && order.getOrderStatus() != 5) {
            List<OrderDetail> orderDetails = orderDetailRepository.findByOrder(order);
            for (OrderDetail orderDetail : orderDetails) {
                Product product = orderDetail.getProduct();
                product.setStock(product.getStock() + orderDetail.getQuantity());
                productRepository.save(product);
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
        Float price = orderDetail.getProduct().getPrice().floatValue();
        if (orderDetail.getProduct().getDiscount() != null) {
            double discountValue = orderDetail.getProduct().getDiscount().getValue();
            price = (float) (price * (1 - discountValue / 100));
        }
        
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
}
