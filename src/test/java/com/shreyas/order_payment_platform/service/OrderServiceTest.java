package com.shreyas.order_payment_platform.service;

import com.shreyas.order_payment_platform.dto.requests.OrderItemRequest;
import com.shreyas.order_payment_platform.dto.requests.OrderRequest;
import com.shreyas.order_payment_platform.dto.responses.OrderResponse;
import com.shreyas.order_payment_platform.entity.IdempotencyKey;
import com.shreyas.order_payment_platform.entity.Order;
import com.shreyas.order_payment_platform.entity.Product;
import com.shreyas.order_payment_platform.entity.User;
import com.shreyas.order_payment_platform.entity.enums.IdempotencyStatus;
import com.shreyas.order_payment_platform.entity.enums.OrderStatus;
import com.shreyas.order_payment_platform.entity.enums.Role;
import com.shreyas.order_payment_platform.exception.IdempotencyConflictException;
import com.shreyas.order_payment_platform.exception.InsufficientStockException;
import com.shreyas.order_payment_platform.repository.IdempotencyKeyRepository;
import com.shreyas.order_payment_platform.repository.OrderRepository;
import com.shreyas.order_payment_platform.repository.ProductRepository;
import com.shreyas.order_payment_platform.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private IdempotencyKeyRepository idempotencyKeyRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    public void createOrder_shouldReturnTotalCorrectly() {

        Product product1 = Product.builder()
                .id(1L)
                .name("Product 1")
                .description("Product 1")
                .price(new java.math.BigDecimal("10.00"))
                .stockQuantity(100)
                .build();

        Product product2 = Product.builder()
                .id(2L)
                .name("Product 2")
                .description("Product 2")
                .price(new java.math.BigDecimal("20.00"))
                .stockQuantity(100)
                .build();

        OrderItemRequest item1 = new OrderItemRequest();
        item1.setProductId(1L);
        item1.setQuantity(2);

        OrderItemRequest item2 = new OrderItemRequest();
        item2.setProductId(2L);
        item2.setQuantity(4);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderItems(List.of(item1, item2));

        User user = User.builder()
                .id(200L)
                .username("testuser")
                .email("testuser@gmail.com")
                .password("testpassword")
                .role(Role.USER)
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");

        when(idempotencyKeyRepository.findByIdempotencyKey("test-key")).thenReturn(Optional.empty());

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));

        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        OrderResponse orderResponse = orderService.createOrder(orderRequest, "test-key", authentication);

        assertThat(orderResponse.totalAmount()).isEqualTo(new java.math.BigDecimal("100.00"));

    }

    @Test
    public void createOrder_shouldReturnInsufficientStockCorrectly() {
        Product product = Product.builder()
                .id(1L)
                .name("Product 1")
                .description("Product 1")
                .price(new java.math.BigDecimal("10.00"))
                .stockQuantity(1)
                .build();

        OrderItemRequest item1 = new OrderItemRequest();
        item1.setProductId(1L);
        item1.setQuantity(2);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderItems(List.of(item1));

        User user = User.builder()
                .id(200L)
                .username("testuser")
                .email("testUser@gmail.com")
                .password("testpassword")
                .role(Role.USER)
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
        when(idempotencyKeyRepository.findByIdempotencyKey("test-key")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> orderService.createOrder(orderRequest, "test-key", authentication))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("Insufficient stock for product: Product 1");
    }

    @Test
    public void createOrder_shouldReturnExistingOrderOnDuplicateKey() {
        OrderItemRequest item1 = new OrderItemRequest();
        item1.setProductId(1L);
        item1.setQuantity(2);

        String requestHash = sha256("1:2|");

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderItems(List.of(item1));

        IdempotencyKey existingKey = IdempotencyKey.builder()
                .idempotencyKey("test-key")
                .requestHash(requestHash)
                .responseBody("42")
                .status(IdempotencyStatus.COMPLETED)
                .build();

        Order mainOrder = Order.builder()
                .id(42L)
                .orderStatus(OrderStatus.CONFIRMED)
                .totalAmount(new java.math.BigDecimal("100.00"))
                .orderItems(new ArrayList<>())
                .build();

        when(idempotencyKeyRepository.findByIdempotencyKey("test-key")).thenReturn(Optional.of(existingKey));
        when(orderRepository.findById(42L)).thenReturn(Optional.of(mainOrder));

        Authentication authentication = mock(Authentication.class);

        OrderResponse orderResponse = orderService.createOrder(orderRequest, "test-key", authentication);

        assertThat(orderResponse.totalAmount()).isEqualByComparingTo("100.00");
    }

    private String sha256(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
        @Test
        public void createOrder_shouldThrowConflictWhenSameKey(){
            IdempotencyKey existingKey = IdempotencyKey.builder()
                    .idempotencyKey("test-key")
                    .requestHash("some-hash")
                    .responseBody("42")
                    .status(IdempotencyStatus.COMPLETED)
                    .build();

            OrderItemRequest item1 = new OrderItemRequest();
            item1.setProductId(1L);
            item1.setQuantity(2);

            OrderRequest orderRequest = new OrderRequest();
            orderRequest.setOrderItems(List.of(item1));

            when(idempotencyKeyRepository.findByIdempotencyKey("test-key")).thenReturn(Optional.of(existingKey));

            Authentication authentication = mock(Authentication.class);
            when(authentication.getName()).thenReturn("testuser");

            assertThatThrownBy(() -> orderService.createOrder(orderRequest, "test-key", authentication))
                    .isInstanceOf(IdempotencyConflictException.class);

        }

}
