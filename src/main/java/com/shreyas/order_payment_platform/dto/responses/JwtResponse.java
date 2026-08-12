package com.shreyas.order_payment_platform.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String username;
    private String role;
}
