package com.subash.product_jwt_copy.repository;

import com.subash.product_jwt_copy.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}