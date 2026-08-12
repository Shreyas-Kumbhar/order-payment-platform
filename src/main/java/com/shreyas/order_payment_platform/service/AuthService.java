package com.shreyas.order_payment_platform.service;

import com.shreyas.order_payment_platform.dto.requests.LoginRequest;
import com.shreyas.order_payment_platform.dto.requests.RegisterRequest;
import com.shreyas.order_payment_platform.dto.responses.JwtResponse;
import com.shreyas.order_payment_platform.entity.User;
import com.shreyas.order_payment_platform.entity.enums.Role;
import com.shreyas.order_payment_platform.repository.UserRepository;
import com.shreyas.order_payment_platform.security.JwtTokenProvider;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public void register(RegisterRequest registerRequest) {


        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new IllegalStateException("Username is already in use");
        }


        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new IllegalStateException("Email is already in use");
        }


        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER)
                .build();


        userRepository.save(user);
    }

    public JwtResponse login(LoginRequest loginRequest) {


        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );


        String token = jwtTokenProvider.generateToken(authentication);

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() ->
                        new IllegalStateException("User not found after authentication"));

        return new JwtResponse(
                token,
                user.getUsername(),
                user.getRole().name()
        );
    }
}