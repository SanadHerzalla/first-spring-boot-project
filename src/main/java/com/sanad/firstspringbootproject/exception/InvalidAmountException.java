package com.sanad.firstspringbootproject.exception;

import java.math.BigDecimal;

public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException(BigDecimal amount) {
        super("Amount must be greater that zero. Received: " + amount);
    }
}
