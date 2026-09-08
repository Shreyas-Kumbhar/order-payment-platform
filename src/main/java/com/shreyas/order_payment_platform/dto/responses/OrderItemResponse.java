package com.shreyas.order_payment_platform.dto.responses;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal purchaseAtPrice
) {}
