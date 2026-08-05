package com.sanad.firstspringbootproject.exception;


public class BankNotFoundException extends RuntimeException {
    public BankNotFoundException(Long id) {
        super("Bank with id " + id + " not found");
    }
}
