package com.subash.product_jwt_copy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Long id;

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotNull(message = "Price is mandatory")
    @Positive(message = "Price must be positive")
    private Double price;

    @NotBlank(message = "Description is mandatory")
    private String description;

    @NotBlank(message = "ImageUrl is mandatory")
    private String imageUrl;

    @NotBlank(message = "SubCategory is mandatory")
    private String subcategory;

    @NotBlank(message = "Category is mandatory")
    private String category;

    private LocalDateTime createdAt;
}