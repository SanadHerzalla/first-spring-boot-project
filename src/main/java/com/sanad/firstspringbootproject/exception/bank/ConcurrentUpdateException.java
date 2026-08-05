package com.sanad.firstspringbootproject.exception.bank;

public class ConcurrentUpdateException extends RuntimeException {
    public ConcurrentUpdateException() {
        super("The resource was changed by another request. "+
                "Refresh the data and try again.");
    }
}
