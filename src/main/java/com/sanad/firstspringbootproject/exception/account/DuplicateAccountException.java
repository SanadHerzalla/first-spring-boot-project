package com.sanad.firstspringbootproject.exception.account;

public class DuplicateAccountException extends  RuntimeException {
    public  DuplicateAccountException(String accountNumber, Long bankId) {
        super("Account " + accountNumber + " already exists in bank with ID " + bankId);
    }
}
