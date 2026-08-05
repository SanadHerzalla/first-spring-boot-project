package com.sanad.firstspringbootproject.dto.account;

import com.sanad.firstspringbootproject.model.AccountType;

import java.math.BigDecimal;

public record AccountResponse(
        Long id,
        String accountNumber,
        String ownerName,
        AccountType accountType,
        BigDecimal balance,
        Long version,
        Long bankId
) {

}
