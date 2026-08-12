package com.sanad.firstspringbootproject.JMS.job;

import java.math.BigDecimal;

public record OperationJob(
        Long operationId,
        String operationType,
        Long bankId,
        String accountNumber,
        BigDecimal amount
) {
}
