package com.shreyas.order_payment_platform.service;

import com.shreyas.order_payment_platform.filter.IdempotencyFilter;
import com.shreyas.order_payment_platform.repository.IdempotencyKeyRepository;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class IdempotencyFilterTest {

    @Mock
    private IdempotencyKeyRepository idempotencyKeyRepository;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private IdempotencyFilter idempotencyFilter;

}
