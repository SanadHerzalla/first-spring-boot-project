package com.sanad.firstspringbootproject.exception;

public class BankHasAccountsException extends RuntimeException {
    public  BankHasAccountsException(long bankId) {
        super("Bank with ID " + bankId + " cannot be deleted because it contains accounts");
    }
}
