package com.shreyas.order_payment_platform.controller;

import com.shreyas.order_payment_platform.dto.responses.UserResponse;
import com.shreyas.order_payment_platform.entity.User;
import com.shreyas.order_payment_platform.repository.UserRepository;
import com.shreyas.order_payment_platform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        UserResponse userResponse= userService.getCurrentUser(authentication.getName());
        return ResponseEntity.ok(userResponse);
    }
}
