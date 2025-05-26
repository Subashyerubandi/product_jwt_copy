package com.subash.product_jwt_copy.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.subash.product_jwt_copy.dto.ProductDTO;
import com.subash.product_jwt_copy.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = {"http://localhost:5173", "http://192.168.1.101:5173"}, allowCredentials = "true")
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class ProductControllerAdmin {

	private final ProductService productService;
	
	@PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.createProduct(productDTO));
    }
	
	@PostMapping("/bulk")
    public ResponseEntity<List<ProductDTO>> createProducts(@Valid @RequestBody List<ProductDTO> productDTOs) {
        List<ProductDTO> savedProducts = productService.createProducts(productDTOs);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProducts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.updateProduct(id, productDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully");
    }
	
}
