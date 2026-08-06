package com.sanad.firstspringbootproject.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(long bankId, String accountNumber) {
        super("Account " + accountNumber + " was not found in bank with ID " + bankId);
    }
}
