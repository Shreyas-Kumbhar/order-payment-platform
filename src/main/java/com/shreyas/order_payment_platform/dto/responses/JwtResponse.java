package com.shreyas.order_payment_platform.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String username;
    private String role;
}
