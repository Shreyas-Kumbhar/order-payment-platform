package com.shreyas.order_payment_platform.entity;

import com.shreyas.order_payment_platform.entity.enums.IdempotencyStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="idempotency_keys")
public class IdempotencyKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="idempotency_key",nullable = false,unique = true)
    private String idempotencyKey;

    @Column(name="request_hash", nullable =false)
    private String requestHash;

    @Lob
    @Column(name="response_body", nullable =false)
    private String responseBody;

    @Enumerated(EnumType.STRING)
    private IdempotencyStatus status;

    @Column(updatable=false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist()
    {
        this.createdAt = LocalDateTime.now();
    }
}
