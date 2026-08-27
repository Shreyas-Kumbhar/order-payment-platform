package com.shreyas.order_payment_platform.controller;

import com.shreyas.order_payment_platform.dto.requests.OrderRequest;
import com.shreyas.order_payment_platform.dto.responses.OrderResponse;
import com.shreyas.order_payment_platform.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest,
                                                     Authentication authentication) {
        return ResponseEntity.ok(orderService.createOrder(orderRequest, authentication));
    }
}
