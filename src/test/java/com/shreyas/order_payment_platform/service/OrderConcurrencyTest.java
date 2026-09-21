package com.shreyas.order_payment_platform.service;

import com.shreyas.order_payment_platform.dto.requests.OrderItemRequest;
import com.shreyas.order_payment_platform.dto.requests.OrderRequest;
import com.shreyas.order_payment_platform.entity.Order;
import com.shreyas.order_payment_platform.entity.Product;
import com.shreyas.order_payment_platform.entity.User;
import com.shreyas.order_payment_platform.exception.InsufficientStockException;
import com.shreyas.order_payment_platform.repository.IdempotencyKeyRepository;
import com.shreyas.order_payment_platform.repository.OrderRepository;
import com.shreyas.order_payment_platform.repository.ProductRepository;
import com.shreyas.order_payment_platform.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    void onlyOneOrderSucceedWhenTwoThreadsRaceConditionForLastStock() throws InterruptedException {

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

        OrderItemRequest orderItemRequest=new OrderItemRequest();
        orderItemRequest.setProductId(1L);
        orderItemRequest.setQuantity(1);

        OrderRequest orderRequest=new OrderRequest();
        orderRequest.setOrderItems(List.of(orderItemRequest));

        Authentication authentication=mock(Authentication.class);
        when(authentication.getName()).thenReturn("test-user");

        when(idempotencyKeyRepository.findByIdempotencyKey("key-1")).thenReturn(Optional.empty());
        when(idempotencyKeyRepository.findByIdempotencyKey("key-2")).thenReturn(Optional.empty());

        when(userRepository.findByUsername("test-user")).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch countDownLatch = new CountDownLatch(1);

        Runnable thread1 = () -> {
            try{
                countDownLatch.await();
                orderService.createOrder(orderRequest, "key-1", authentication);
                successCount.incrementAndGet();
            }
            catch(InsufficientStockException e){
                failureCount.incrementAndGet();
            }
            catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }
        };


        Runnable thread2 = () -> {
            try{
                countDownLatch.await();
                orderService.createOrder(orderRequest, "key-2", authentication);
                successCount.incrementAndGet();
            }
            catch(InsufficientStockException e){
                failureCount.incrementAndGet();
            }
            catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }
        };

    }
}
