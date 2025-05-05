package com.example.electronics_store.service.impl;

import com.example.electronics_store.dto.CartDTO;
import com.example.electronics_store.dto.CartItemDTO;
import com.example.electronics_store.model.Cart;
import com.example.electronics_store.model.CartItem;
import com.example.electronics_store.model.Product;
import com.example.electronics_store.model.User;
import com.example.electronics_store.repository.CartItemRepository;
import com.example.electronics_store.repository.CartRepository;
import com.example.electronics_store.repository.ProductRepository;
import com.example.electronics_store.repository.UserRepository;
import com.example.electronics_store.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    
    @Autowired
    public CartServiceImpl(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            UserRepository userRepository,
            ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public CartDTO getCartByUserId(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get or create cart
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setCartItems(new ArrayList<>());
                    return cartRepository.save(newCart);
                });
        
        return mapCartToDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO addProductToCart(Integer userId, Integer productId, Integer quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        // Check if product is in stock
        if (product.getStock() < quantity) {
            throw new RuntimeException("Not enough stock available");
        }
        
        // Get or create cart
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setCartItems(new ArrayList<>());
                    return cartRepository.save(newCart);
                });
        CartItem cartItem;
        // Check if product already exists in cart
        Optional<CartItem> existingCartItem = cartItemRepository.findByCartAndProduct(cart, product);

        if (existingCartItem.isPresent()) {
            cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            Float price = product.getDiscount() != null
                    ? (float) (product.getPrice() * (1 - product.getDiscount().getValue() / 100))
                    : product.getPrice().floatValue();
            cartItem.setPrice(price);
            cart.getCartItems().add(cartItem);
        }

        cartItemRepository.save(cartItem);
        cartRepository.save(cart);

        cart = cartRepository.findByUser(user).orElseThrow();

        return mapCartToDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO updateCartItemQuantity(Integer userId, Integer cartItemId, Integer quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        // Verify that the cart item belongs to the user's cart
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Cart item does not belong to the user's cart");
        }
        
        // Check if product is in stock
        if (cartItem.getProduct().getStock() < quantity) {
            throw new RuntimeException("Not enough stock available");
        }
        
        if (quantity <= 0) {
            // Remove item if quantity is 0 or negative
            cartItemRepository.delete(cartItem);
        } else {
            // Update quantity
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }
        
        return mapCartToDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO removeCartItem(Integer userId, Integer cartItemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        // Verify that the cart item belongs to the user's cart
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Cart item does not belong to the user's cart");
        }
        // Remove the item from cart's collection
        cart.getCartItems().remove(cartItem);
        cartItem.setCart(null); // Unlink the relationship

        // Delete the cart item
        cartItemRepository.delete(cartItem);
        // Save the updated cart
        cart = cartRepository.save(cart);
        // Refresh cart data
        cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        return mapCartToDTO(cart);
    }

    @Override
    @Transactional
    public void clearCart(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        
        cartItemRepository.deleteAllByCartId(cart.getId());
    }

    @Override
    public Long getCartItemCount(Integer userId) {
        return cartRepository.countCartItemsByUserId(userId);
    }

    @Override
    public Float calculateCartTotal(Integer userId) {
        Float total = cartRepository.calculateCartTotalByUserId(userId);
        return total != null ? total : 0f;
    }

    @Override
    public Optional<Cart> getCartEntityByUserId(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return cartRepository.findByUser(user);
    }
    
    // Helper methods
    private CartDTO mapCartToDTO(Cart cart) {
        // Đảm bảo cartItems không null
        List<CartItem> cartItems = cart.getCartItems() != null ? cart.getCartItems() : new ArrayList<>();

        // Map sang DTOs
        List<CartItemDTO> cartItemDTOs = cartItems.stream()
                .map(this::mapCartItemToDTO)
                .collect(Collectors.toList());

        // Tính tổng số lượng sản phẩm
        Integer totalItems = cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        // Tính tổng tiền
        Float totalPrice = cartItems.stream()
                .map(item -> item.getPrice() * item.getQuantity())
                .reduce(0f, Float::sum);

        return CartDTO.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .createAt(cart.getCreateAt())
                .items(cartItemDTOs)
                .totalPrice(totalPrice)
                .totalItems(totalItems)
                .build();
    }

    private CartItemDTO mapCartItemToDTO(CartItem cartItem) {
        // Tính totalPrice cho từng item
        Float totalPrice = cartItem.getPrice() * cartItem.getQuantity();

        return CartItemDTO.builder()
                .id(cartItem.getId())
                .productId(cartItem.getProduct().getId())
                .productName(cartItem.getProduct().getName())
                .productImage(cartItem.getProduct().getImage())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getPrice())
                .totalPrice(totalPrice)
                .createAt(cartItem.getCreateAt())
                .stock(cartItem.getProduct().getStock())
                .build();
    }
}
