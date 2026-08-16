package com.shreyas.order_payment_platform.controller;

import com.shreyas.order_payment_platform.dto.requests.ProductRequests;
import com.shreyas.order_payment_platform.dto.responses.ProductResponse;
import com.shreyas.order_payment_platform.entity.Product;
import com.shreyas.order_payment_platform.repository.ProductRepository;
import com.shreyas.order_payment_platform.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(){
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id){
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequests requests){
        return ResponseEntity.ok(productService.createProduct(requests));
    }
}
