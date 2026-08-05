package com.sanad.firstspringbootproject.exception;

import com.sanad.firstspringbootproject.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BankNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleBankNotFoundException(BankNotFoundException exception, HttpServletRequest request) {
        ApiErrorResponse response = new ApiErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), "Not Found", exception.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(DuplicateBankException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateBankException(DuplicateBankException exception, HttpServletRequest request) {
        ApiErrorResponse response = new ApiErrorResponse(LocalDateTime.now(), HttpStatus.CONFLICT.value(), "Conflict", exception.getMessage(), request.getRequestURI());
        return  ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ApiErrorResponse> handleOptimisticLockingFailure(
            ObjectOptimisticLockingFailureException exception,
            HttpServletRequest request
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Conflict",
                "The banks was changed by another request. " + "Refresh the data and try again.",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}
