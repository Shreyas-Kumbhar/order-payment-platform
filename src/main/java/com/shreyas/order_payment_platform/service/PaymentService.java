package com.shreyas.order_payment_platform.service;

import com.shreyas.order_payment_platform.dto.responses.PaymentResponse;
import com.shreyas.order_payment_platform.entity.Order;
import com.shreyas.order_payment_platform.entity.Payment;
import com.shreyas.order_payment_platform.entity.enums.OrderStatus;
import com.shreyas.order_payment_platform.entity.enums.PaymentStatus;
import com.shreyas.order_payment_platform.exception.ResourceNotFoundException;
import com.shreyas.order_payment_platform.repository.OrderRepository;
import com.shreyas.order_payment_platform.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public PaymentResponse processPayment(Long orderId){

        Order order= orderRepository.findById(orderId).orElseThrow(()-> new ResourceNotFoundException("Order not found with id: "+orderId));

        var existing= paymentRepository.findByOrderId(orderId);
        if(existing.isPresent()){
            return toResponse(existing.get());
        }

        Payment payment= Payment.builder()
                .order(order)
                .amount(order.getTotalAmount())
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        payment=paymentRepository.save(payment);

        payment.setPaymentStatus(PaymentStatus.PROCESSING);

        boolean success= mockPaymentProcessor();

        if(success){
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            order.setOrderStatus(OrderStatus.CONFIRMED);
        }
        else{
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setFailureReason("Payment declined by mock processor");
            order.setOrderStatus(OrderStatus.FAILED);
        }
        orderRepository.save(order);
        return toResponse(paymentRepository.save(payment));

    }

    public PaymentResponse getPaymentById(Long id){
        Payment payment=paymentRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Payment not found with id: "+id));
        return toResponse(payment);
    }

    private boolean mockPaymentProcessor(){
        return new Random().nextInt(10)<8;
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getPaymentStatus().name(),
                payment.getOrder().getId(),
                payment.getAmount(),
                payment.getFailureReason(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }

}
