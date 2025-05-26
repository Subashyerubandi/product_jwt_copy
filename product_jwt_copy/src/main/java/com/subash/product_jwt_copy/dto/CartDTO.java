package com.subash.product_jwt_copy.dto;

import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDTO {
    private Long id;
    private Long userId;
    private Set<CartItemDTO> cartItems;
    private int numberOfItems;
    private double grandTotal;
}