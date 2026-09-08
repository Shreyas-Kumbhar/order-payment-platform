package com.shreyas.order_payment_platform.dto.responses;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity
) {}
