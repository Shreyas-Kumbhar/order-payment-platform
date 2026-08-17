package com.shreyas.order_payment_platform.dto.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {
    @NotEmpty(message = "Order has at least one item")
    @Valid
    private List<OrderItemRequest> orderItems;
}
