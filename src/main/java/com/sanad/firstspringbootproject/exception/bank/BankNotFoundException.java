package com.sanad.firstspringbootproject.exception.bank;


public class BankNotFoundException extends RuntimeException {
    public BankNotFoundException(Long id) {
        super("Bank with ID " + id + " was not found");
    }
}
