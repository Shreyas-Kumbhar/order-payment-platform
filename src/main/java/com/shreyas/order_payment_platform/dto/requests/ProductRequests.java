package com.shreyas.order_payment_platform.dto.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequests {
    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @DecimalMin(value = "0.01", inclusive = false, message = "Price must be greater than zero")
    private BigDecimal price;

    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;
}
