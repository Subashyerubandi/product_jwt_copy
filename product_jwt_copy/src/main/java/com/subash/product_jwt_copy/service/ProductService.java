package com.subash.product_jwt_copy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.subash.product_jwt_copy.dto.ProductDTO;
import com.subash.product_jwt_copy.model.Product;
import com.subash.product_jwt_copy.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
	
    private final ProductRepository productRepository;

    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .description(productDTO.getDescription())
                .imageUrl(productDTO.getImageUrl())
                .subcategory(productDTO.getSubcategory())
                .category(productDTO.getCategory())
                .createdAt(productDTO.getCreatedAt())
                .build();
        
        Product savedProduct = productRepository.save(product);
        return mapToDTO(savedProduct);
    }

    @Transactional
    public List<ProductDTO> createProducts(List<ProductDTO> productDTOs) {
        List<Product> products = productDTOs.stream()
                .map(dto -> Product.builder()
                        .name(dto.getName())
                        .price(dto.getPrice())
                        .description(dto.getDescription())
                        .imageUrl(dto.getImageUrl())
                        .subcategory(dto.getSubcategory())
                        .category(dto.getCategory())
                        .createdAt(dto.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        
        List<Product> savedProducts = productRepository.saveAll(products);
        return savedProducts.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return mapToDTO(product);
    }

    public List<ProductDTO> getProductsByCategory(String category) {
        return productRepository.findByCategory(category).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getProductsBySubcategory(String subcategory) {
        return productRepository.findBySubcategory(subcategory).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setDescription(productDTO.getDescription());
        product.setImageUrl(productDTO.getImageUrl());
        product.setSubcategory(productDTO.getSubcategory());
        product.setCategory(productDTO.getCategory());
        product.setCreatedAt(productDTO.getCreatedAt());
        
        Product updatedProduct = productRepository.save(product);
        return mapToDTO(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    private ProductDTO mapToDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .subcategory(product.getSubcategory())
                .category(product.getCategory())
                .createdAt(product.getCreatedAt())
                .build();
    }
}