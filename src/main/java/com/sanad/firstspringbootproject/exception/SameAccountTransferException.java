package com.sanad.firstspringbootproject.exception;

public class SameAccountTransferException extends RuntimeException {
    public SameAccountTransferException() {
        super("Source and destination accounts must be different");
    }
}
