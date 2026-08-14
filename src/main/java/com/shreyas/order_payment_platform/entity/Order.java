package com.shreyas.order_payment_platform.entity;

import com.shreyas.order_payment_platform.entity.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal Totalamount;

    @Builder.Default
    @OneToMany(
            mappedBy = "order",
            cascade=CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItem> orderItems=new ArrayList<>();

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate()
    {
        this.createdAt = LocalDateTime.now();
        if(this.orderStatus==null){
            this.orderStatus=OrderStatus.PENDING;
        }
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
}
