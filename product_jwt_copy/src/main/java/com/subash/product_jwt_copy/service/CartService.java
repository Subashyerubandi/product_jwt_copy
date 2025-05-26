package com.subash.product_jwt_copy.service;

import com.subash.product_jwt_copy.dto.CartDTO;
import com.subash.product_jwt_copy.dto.CartItemDTO;
import com.subash.product_jwt_copy.exception.CartNotFoundException;
import com.subash.product_jwt_copy.model.Cart;
import com.subash.product_jwt_copy.model.CartItem;
import com.subash.product_jwt_copy.model.Product;
import com.subash.product_jwt_copy.model.User;
import com.subash.product_jwt_copy.repository.CartItemRepository;
import com.subash.product_jwt_copy.repository.CartRepository;
import com.subash.product_jwt_copy.repository.ProductRepository;
import com.subash.product_jwt_copy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Transactional
    public CartDTO createCartForUser() {
        User user = getCurrentUser();
        Optional<Cart> existingCart = cartRepository.findByIdWithItems(user.getId());
        if (existingCart.isPresent()) {
            return convertToCartDTO(existingCart.get());
        }

        Cart cart = Cart.builder()
                .id(user.getId())
                .user(user)
                .cartItems(new HashSet<>())
                .build();

        cart = cartRepository.save(cart);
        return convertToCartDTO(cart);
    }

    @Transactional
    public CartDTO addItemToCart(Long productId, Integer quantity) {
        User user = getCurrentUser();
        
        Cart cart = cartRepository.findByIdWithItems(user.getId())
                .orElseGet(() -> {
                    return cartRepository.save(Cart.builder()
                            .id(user.getId())
                            .user(user)
                            .cartItems(new HashSet<>())
                            .build());
                });

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItemRepository.save(cartItem);
        } else {
            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .build();
            cart.getCartItems().add(cartItem);
            cartItemRepository.save(cartItem);
        }

        cartRepository.save(cart);
        return convertToCartDTO(cart);
    }

    @Transactional
    public CartDTO updateCartItemQuantity(Long cartItemId, Integer quantity) {
        User user = getCurrentUser();
        
        Cart cart = cartRepository.findByIdWithItems(user.getId())
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + user.getEmail()));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .filter(item -> item.getCart().getId().equals(user.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found or does not belong to user"));

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        return convertToCartDTO(cart);
    }

    @Transactional
    public CartDTO removeItemFromCart(Long cartItemId) {
        User user = getCurrentUser();
        
        Cart cart = cartRepository.findByIdWithItems(user.getId())
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + user.getEmail()));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .filter(item -> item.getCart().getId().equals(user.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found or does not belong to user"));

        cart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
        cartRepository.save(cart);
        return convertToCartDTO(cart);
    }

    public CartDTO getCart() {
        User user = getCurrentUser();
        Cart cart = cartRepository.findByIdWithItems(user.getId())
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + user.getEmail()));
        return convertToCartDTO(cart);
    }

    private CartDTO convertToCartDTO(Cart cart) {
        Set<CartItemDTO> cartItemDTOs = cart.getCartItems().stream()
                .map(item -> CartItemDTO.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .imageUrl(item.getProduct().getImageUrl())
                        .actualPrice(item.getProduct().getPrice())
                        .totalPrice(item.getProduct().getPrice() * item.getQuantity())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toSet());

        Integer totalQuantity = cartItemDTOs.stream()
                .mapToInt(CartItemDTO::getQuantity)
                .sum();

        Double totalPrice = cartItemDTOs.stream()
                .mapToDouble(CartItemDTO::getTotalPrice)
                .sum();

        return CartDTO.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .cartItems(cartItemDTOs)
                .numberOfItems(totalQuantity)
                .grandTotal(totalPrice)
                .build();
    }
}