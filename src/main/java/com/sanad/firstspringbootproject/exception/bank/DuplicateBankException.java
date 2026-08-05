package com.sanad.firstspringbootproject.exception.bank;

public class DuplicateBankException extends RuntimeException {
    public DuplicateBankException(String name) {
        super("A bank named '" + name + "' already exists");
    }
}
