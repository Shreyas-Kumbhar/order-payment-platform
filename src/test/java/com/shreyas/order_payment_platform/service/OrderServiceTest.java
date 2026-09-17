package com.shreyas.order_payment_platform.service;

import com.shreyas.order_payment_platform.dto.requests.OrderItemRequest;
import com.shreyas.order_payment_platform.dto.requests.OrderRequest;
import com.shreyas.order_payment_platform.entity.Product;
import com.shreyas.order_payment_platform.repository.IdempotencyKeyRepository;
import com.shreyas.order_payment_platform.repository.OrderRepository;
import com.shreyas.order_payment_platform.repository.ProductRepository;
import com.shreyas.order_payment_platform.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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

        Product product1=Product.builder()
                .id(1L)
                .name("Product 1")
                .description("Product 1")
                .price(new java.math.BigDecimal("10.00"))
                .stockQuantity(100)
                .build();

        Product product2=Product.builder()
                .id(2L)
                .name("Product 2")
                .description("Product 2")
                .price(new java.math.BigDecimal("20.00"))
                .stockQuantity(100)
                .build();

        OrderItemRequest item1=new OrderItemRequest();
        item1.setProductId(1L);
        item1.setQuantity(2);

        OrderItemRequest item2=new OrderItemRequest();
        item1.setProductId(2L);
        item1.setQuantity(4);

        OrderRequest orderRequest=new OrderRequest();
        orderRequest.setOrderItems(List.of(item1, item2));


    }

}
