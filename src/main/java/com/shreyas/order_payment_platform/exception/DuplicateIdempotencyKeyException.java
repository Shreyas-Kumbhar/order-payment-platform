package com.shreyas.order_payment_platform.exception;

public class DuplicateIdempotencyKeyException extends RuntimeException{
    public DuplicateIdempotencyKeyException(String message) {
        super(message);
    }
}
