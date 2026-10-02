package com.shreyas.order_payment_platform.service;

import com.shreyas.order_payment_platform.dto.responses.PaymentResponse;
import com.shreyas.order_payment_platform.entity.Order;
import com.shreyas.order_payment_platform.entity.Payment;
import com.shreyas.order_payment_platform.entity.enums.OrderStatus;
import com.shreyas.order_payment_platform.repository.OrderRepository;
import com.shreyas.order_payment_platform.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    public void proccesspayment_shouldReturnSuccessOrFailure() {

        Order order=Order.builder()
                .id(1L)
                .orderItems(new ArrayList<>())
                .orderStatus(OrderStatus.PENDING)
                .totalAmount(new BigDecimal("100.00"))
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i->i.getArguments(0));

        PaymentResponse response=paymentService.processPayment(1L);

        assertThat(response.status()).isIn("SUCCESS","FAILURE");
        assertThat(response.amount()).isEqualByComparingTo("100.00");
    }

}
