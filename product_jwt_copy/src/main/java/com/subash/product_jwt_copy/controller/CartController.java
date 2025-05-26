package com.subash.product_jwt_copy.controller;

import com.subash.product_jwt_copy.dto.CartDTO;
import com.subash.product_jwt_copy.service.CartService;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {
    
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartDTO> createCart() {
        CartDTO cartDTO = cartService.createCartForUser();
        return ResponseEntity.ok(cartDTO);
    }

    @PostMapping("/items")
    public ResponseEntity<CartDTO> addItemToCart(
            @RequestParam @NotNull Long productId,
            @RequestParam @Positive(message = "Quantity must be positive") Integer quantity) {
        CartDTO cartDTO = cartService.addItemToCart(productId, quantity);
        return ResponseEntity.ok(cartDTO);
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartDTO> updateCartItemQuantity(
            @PathVariable Long cartItemId,
            @RequestParam @Positive(message = "Quantity must be positive") Integer quantity) {
        CartDTO cartDTO = cartService.updateCartItemQuantity(cartItemId, quantity);
        return ResponseEntity.ok(cartDTO);
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartDTO> removeItemFromCart(
            @PathVariable Long cartItemId) {
        CartDTO cartDTO = cartService.removeItemFromCart(cartItemId);
        return ResponseEntity.ok(cartDTO);
    }

    @GetMapping
    public ResponseEntity<CartDTO> getCart() {
        CartDTO cartDTO = cartService.getCart();
        return ResponseEntity.ok(cartDTO);
    }
}