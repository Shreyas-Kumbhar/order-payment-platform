package com.shreyas.order_payment_platform.filter;

import com.shreyas.order_payment_platform.repository.IdempotencyKeyRepository;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IdempotencyFilterTest {

    @Mock
    private IdempotencyKeyRepository idempotencyKeyRepository;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private IdempotencyFilter idempotencyFilter;

    @Test
    void shouldThrow400WhenIdempotencyKeyNotFound() throws Exception {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setMethod("POST");
        mockHttpServletRequest.setRequestURI("/api/orders");

        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        idempotencyFilter.doFilterInternal(mockHttpServletRequest, mockHttpServletResponse, filterChain);

        assertThat(mockHttpServletResponse.getStatus()).isEqualTo(400);
        assertThat(mockHttpServletResponse.getContentAsString()).contains("Idempotency-Key header is missing");

        verify(filterChain, never()).doFilter(any(), any());

    }

    @Test
    void shouldPassWhenIdempotencyHeaderExist() throws Exception {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setMethod("POST");
        mockHttpServletRequest.setRequestURI("/api/orders");
        mockHttpServletRequest.addHeader("Idempotency-Key", "test-key");

        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        idempotencyFilter.doFilterInternal(mockHttpServletRequest, mockHttpServletResponse, filterChain);

        assertThat(mockHttpServletResponse.getStatus()).isEqualTo(200);

        verify(filterChain, times(1)).doFilter(mockHttpServletRequest, mockHttpServletResponse);
    }

}
