package com.subash.product_jwt_copy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.subash.product_jwt_copy.dto.ProductDTO;
import com.subash.product_jwt_copy.service.ProductService;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:5173", "http://192.168.1.101:5173"}, allowCredentials = "true")
@RestController
@RequestMapping("/api/user/products")
@RequiredArgsConstructor
public class ProductController {
	
	private final ProductService productService;
	
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getProducts(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "subcategory", required = false) String subcategory) {
        if (category != null) {
            return new ResponseEntity<>(productService.getProductsByCategory(category), HttpStatus.OK);
        } else if (subcategory != null) {
            return new ResponseEntity<>(productService.getProductsBySubcategory(subcategory), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        ProductDTO product = productService.getProductById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

}