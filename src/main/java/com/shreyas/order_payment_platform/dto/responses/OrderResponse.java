package com.shreyas.order_payment_platform.dto.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String status,
        BigDecimal totalAmount,
        List<OrderItemResponse> items,
        LocalDateTime createdAt
) {}
