package com.sanad.firstspringbootproject.exception;

public class IdempotencyConflictException extends RuntimeException {
    public IdempotencyConflictException(String idempotencyKey) {
        super("Idempotency key '" + idempotencyKey + "' was already used for a different request");
    }
}
