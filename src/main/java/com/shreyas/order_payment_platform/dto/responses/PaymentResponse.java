package com.shreyas.order_payment_platform.dto.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public record PaymentResponse(
            Long id,
            String status,
            Long orderId,
            BigDecimal amount,
            String failureReason,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    )
{}

