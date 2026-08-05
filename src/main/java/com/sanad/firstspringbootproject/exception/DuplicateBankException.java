package com.sanad.firstspringbootproject.exception;

public class DuplicateBankException extends RuntimeException {
    public DuplicateBankException(String name) {
        super("A bank name: '" + name + "' aleady exists");
    }
}
