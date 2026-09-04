package com.shreyas.order_payment_platform.service;

import com.shreyas.order_payment_platform.entity.*;
import com.shreyas.order_payment_platform.entity.enums.IdempotencyStatus;
import com.shreyas.order_payment_platform.exception.IdempotencyConflictException;
import com.shreyas.order_payment_platform.repository.IdempotencyKeyRepository;
import jakarta.transaction.Transactional;
import com.shreyas.order_payment_platform.dto.requests.OrderItemRequest;
import com.shreyas.order_payment_platform.dto.requests.OrderRequest;
import com.shreyas.order_payment_platform.dto.responses.OrderItemResponse;
import com.shreyas.order_payment_platform.dto.responses.OrderResponse;
import com.shreyas.order_payment_platform.entity.enums.OrderStatus;
import com.shreyas.order_payment_platform.exception.InsufficientStockException;
import com.shreyas.order_payment_platform.exception.ResourceNotFoundException;
import com.shreyas.order_payment_platform.repository.OrderRepository;
import com.shreyas.order_payment_platform.repository.ProductRepository;
import com.shreyas.order_payment_platform.repository.UserRepository;
import tools.jackson.core.JacksonException;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public OrderResponse processOrder(OrderRequest request, String idempotencyKey, Authentication authentication) {

        String requestHash=hashRequest(request);

        var existing=idempotencyKeyRepository.findByIdempotencyKey(idempotencyKey);

        if(existing.isPresent()){
            IdempotencyKey record=existing.get();
            if(!record.getRequestHash().equals(requestHash)){
                throw new IdempotencyConflictException("Idempotency key already used with a different request payload.");
            }
            return deserializeResponse(record.getResponseBody());
        }

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
        OrderResponse response=toResponse(savedOrder);
        savedIdempotencyKey(idempotencyKey, requestHash, response);
        return response;
    }

    private OrderResponse deserializeResponse(String response) {
        try{
            return objectMapper.readValue(response, OrderResponse.class);
        }
        catch (JacksonException e){
            throw new RuntimeException("Failed to deserialize response body", e);
        }
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

    private String hashRequest(OrderRequest request) {
        try{
            String json = objectMapper.writeValueAsString(request);

            MessageDigest digest=MessageDigest.getInstance("SHA-256");

            byte[] hash=digest.digest(json.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Error occurred while hashing the request", e);
        }
    }

    private void savedIdempotencyKey(String idempotencyKey, String requestHash, OrderResponse response) {
        try{
            String responseJson=objectMapper.writeValueAsString(response);
            IdempotencyKey record=IdempotencyKey.builder()
                    .idempotencyKey(idempotencyKey)
                    .requestHash(requestHash)
                    .status(IdempotencyStatus.COMPLETED)
                    .responseBody(responseJson)
                    .createdAt(LocalDateTime.now())
                    .build();

            idempotencyKeyRepository.save(record);
        }
        catch (JacksonException e){
            throw new IllegalStateException("Error occurred while serializing the response", e);
        }
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
