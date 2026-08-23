package com.sanad.firstspringbootproject.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class MoneyOperationTest {

    @Test
    void shouldCreateMoneyOperationInProcessingStatus() {
        MoneyOperation operation = new MoneyOperation(
                "idempotency-key",
                MoneyOperationType.DEPOSIT,
                1L,
                "1111",
                null,
                null,
                new BigDecimal("100.00")
        );

        assertEquals("idempotency-key", operation.getIdempotencyKey());
        assertEquals(MoneyOperationType.DEPOSIT, operation.getOperationType());
        assertEquals(MoneyOperationStatus.PROCESSING, operation.getStatus());
        assertEquals(new BigDecimal("100.00"), operation.getAmount());
        assertNotNull(operation.getCreatedAt());
        assertNull(operation.getCompletedAt());
    }

    @Test
    void shouldCompleteOperation() {
        MoneyOperation operation = new MoneyOperation(
                "key", MoneyOperationType.DEPOSIT, 1L, "1111", null, null, new BigDecimal("100")
        );
        BigDecimal resultingBalance = new BigDecimal("500");

        operation.complete(resultingBalance);

        assertEquals(MoneyOperationStatus.COMPLETED, operation.getStatus());
        assertEquals(resultingBalance, operation.getResultingBalance());
        assertNotNull(operation.getCompletedAt());
    }

    @Test
    void shouldFailOperation() {
        MoneyOperation operation = new MoneyOperation(
                "key", MoneyOperationType.DEPOSIT, 1L, "1111", null, null, new BigDecimal("100")
        );

        operation.fail();

        assertEquals(MoneyOperationStatus.FAILED, operation.getStatus());
        assertNotNull(operation.getCompletedAt());
    }

    @Test
    void shouldMatchOperationDetails() {
        MoneyOperation operation = new MoneyOperation(
                "key",
                MoneyOperationType.TRANSFER,
                1L,
                "1111",
                2L,
                "2222",
                new BigDecimal("100.00")
        );

        assertTrue(operation.matches(
                MoneyOperationType.TRANSFER,
                1L,
                "1111",
                2L,
                "2222",
                new BigDecimal("100.00")
        ));

        assertFalse(operation.matches(
                MoneyOperationType.WITHDRAW,
                1L,
                "1111",
                2L,
                "2222",
                new BigDecimal("100.00")
        ));
    }
}
