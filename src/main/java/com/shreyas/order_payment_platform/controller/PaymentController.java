package com.shreyas.order_payment_platform.controller;

import com.shreyas.order_payment_platform.dto.responses.PaymentResponse;
import com.shreyas.order_payment_platform.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/{id}")
    public ResponseEntity<PaymentResponse> processPayment(@PathVariable Long id){
        return ResponseEntity.ok(paymentService.processPayment(id));
    }
}
