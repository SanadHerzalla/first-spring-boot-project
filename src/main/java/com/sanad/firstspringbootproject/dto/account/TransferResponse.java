package com.sanad.firstspringbootproject.dto.account;

import java.math.BigDecimal;

public record TransferResponse(
        AccountResponse sourceAccount,
        AccountResponse destinationAccount,
        BigDecimal amount
) {
}
