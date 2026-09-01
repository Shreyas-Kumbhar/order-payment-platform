package com.shreyas.order_payment_platform.service;

import com.shreyas.order_payment_platform.repository.IdempotencyKeyRepository;
import jakarta.transaction.Transactional;
import com.shreyas.order_payment_platform.dto.requests.OrderItemRequest;
import com.shreyas.order_payment_platform.dto.requests.OrderRequest;
import com.shreyas.order_payment_platform.dto.responses.OrderItemResponse;
import com.shreyas.order_payment_platform.dto.responses.OrderResponse;
import com.shreyas.order_payment_platform.entity.Order;
import com.shreyas.order_payment_platform.entity.OrderItem;
import com.shreyas.order_payment_platform.entity.Product;
import com.shreyas.order_payment_platform.entity.User;
import com.shreyas.order_payment_platform.entity.enums.OrderStatus;
import com.shreyas.order_payment_platform.exception.InsufficientStockException;
import com.shreyas.order_payment_platform.exception.ResourceNotFoundException;
import com.shreyas.order_payment_platform.repository.OrderRepository;
import com.shreyas.order_payment_platform.repository.ProductRepository;
import com.shreyas.order_payment_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final IdempotencyKeyRepository idempotencyKeyRepository;

    @Transactional
    public OrderResponse createOrder(OrderRequest request, Authentication authentication) {

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(()-> new ResourceNotFoundException
                        ("User not found with username: " + authentication.getName()));

        Order order = Order.builder()
                .user(user)
                .orderStatus(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal total=BigDecimal.ZERO;

        for(OrderItemRequest itemRequest : request.getOrderItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(()-> new ResourceNotFoundException
                            ("Product not found with id: " + itemRequest.getProductId()));

            if(product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new InsufficientStockException
                        ("Insufficient stock for product: " + product.getName());
            }

            product.setStockQuantity(product.getStockQuantity() - itemRequest.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .purchaseAtPrice(product.getPrice())
                    .build();

            order.addOrderItem(orderItem);

            total = total.add(product.getPrice().multiply
                    (BigDecimal.valueOf(itemRequest.getQuantity())));
        }
        order.setTotalAmount(total);
        Order savedOrder=orderRepository.save(order);
        return toResponse(savedOrder);
    }

    public List<OrderResponse> getMyOrders(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(()-> new ResourceNotFoundException
                        ("User not found with username: " + authentication.getName()));

        return orderRepository.findByUser(user)
                .stream().map(this::toResponse)
                .toList();
    }

    public OrderResponse getOrderById(Long id) {
        Order order= orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return toResponse(order);
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> orderItemResponses = order.getOrderItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPurchaseAtPrice()
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getOrderStatus().name(),
                order.getTotalAmount(),
                orderItemResponses,
                order.getCreatedAt()
        );
    }

}
