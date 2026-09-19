package com.shreyas.order_payment_platform.service;

import com.shreyas.order_payment_platform.entity.Product;
import com.shreyas.order_payment_platform.entity.User;
import com.shreyas.order_payment_platform.repository.IdempotencyKeyRepository;
import com.shreyas.order_payment_platform.repository.OrderRepository;
import com.shreyas.order_payment_platform.repository.ProductRepository;
import com.shreyas.order_payment_platform.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderConcurrencyTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private IdempotencyKeyRepository idempotencyKeyRepository;

    @Mock
    private OrderService orderService;

    @Test
    void onlyOneOrderSuceedWhenTwoThreadsRaceConditionForLastStock() throws InterruptedException {

        Product product = Product.builder()
                .id(1L)
                .name("Product 1")
                .description("Product 1")
                .price(new java.math.BigDecimal("10.0"))
                .stockQuantity(1)
                .build();

        User user=User.builder()
                .id(1L)
                .username("test-user")
                .build();


    }
}
