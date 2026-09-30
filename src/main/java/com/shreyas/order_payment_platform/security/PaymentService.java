package com.shreyas.order_payment_platform.security;

import com.shreyas.order_payment_platform.dto.responses.PaymentResponse;
import com.shreyas.order_payment_platform.entity.Order;
import com.shreyas.order_payment_platform.entity.Payment;
import com.shreyas.order_payment_platform.entity.enums.PaymentStatus;
import com.shreyas.order_payment_platform.exception.ResourceNotFoundException;
import com.shreyas.order_payment_platform.repository.OrderRepository;
import com.shreyas.order_payment_platform.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public PaymentResponse processPayment(Long orderid){

        Order order= orderRepository.findById(orderid).orElseThrow(()-> new ResourceNotFoundException("Order not found with id: "+orderid));

        var existing= paymentRepository.findByOrderId(orderid);
        if(existing.isPresent()){
            return toResponse(existing.get());
        }

        Payment payment= Payment.builder()
                .order(order)
                .amount(order.getTotalAmount())
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        Payment savedPayment=paymentRepository.save(payment);

    }

    private PaymentResponse toResponse(Order order){

    }
}
