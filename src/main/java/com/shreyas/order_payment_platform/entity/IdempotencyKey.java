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
    private String key;
    private String requestHash;

    @Lob
    private String responseBody;

    @Enumerated(EnumType.STRING)
    private IdempotencyStatus status;

    private LocalDateTime createdAt;
}
