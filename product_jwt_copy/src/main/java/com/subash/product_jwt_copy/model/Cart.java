package com.subash.product_jwt_copy.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "cartItems")
@EqualsAndHashCode(exclude = "cartItems")
@Entity
@Table(name = "carts")
public class Cart {
    @Id
    private Long id; // Same as user_id to enforce one-to-one relationship with User

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Maps the id to the user's id
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartItem> cartItems = new HashSet<>();
    
    @Version
    private Long version;

}