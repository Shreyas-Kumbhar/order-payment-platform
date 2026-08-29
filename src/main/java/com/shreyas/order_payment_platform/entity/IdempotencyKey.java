package com.shreyas.order_payment_platform.entity;

import com.shreyas.order_payment_platform.entity.enums.IdempotencyStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
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
