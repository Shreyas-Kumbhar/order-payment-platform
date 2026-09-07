package com.shreyas.order_payment_platform.service;

import com.shreyas.order_payment_platform.dto.responses.UserResponse;
import com.shreyas.order_payment_platform.entity.User;
import com.shreyas.order_payment_platform.exception.ResourceNotFoundException;
import com.shreyas.order_payment_platform.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserResponse getCurrentUser(String username) {
        User user =userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
