package com.sanad.firstspringbootproject.exception;

public class OperationInProgressException extends RuntimeException {
    public OperationInProgressException(String idempotencyKey) {
        super("The operation with idempotency key '" + idempotencyKey + "' is still being processed");
    }
}
