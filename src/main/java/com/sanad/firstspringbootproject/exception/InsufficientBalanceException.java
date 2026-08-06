package com.sanad.firstspringbootproject.exception;

import java.math.BigDecimal;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String accountNumber, BigDecimal balance) {
        super("Insufficient balance for account '" + accountNumber + "'. Current balance: " + balance);
    }
}
