package com.shreyas.order_payment_platform.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String role;
}
