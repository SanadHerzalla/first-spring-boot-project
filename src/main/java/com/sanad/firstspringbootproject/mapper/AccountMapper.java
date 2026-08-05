package com.sanad.firstspringbootproject.mapper;

import com.sanad.firstspringbootproject.dto.account.AccountResponse;
import com.sanad.firstspringbootproject.model.Account;
import org.springframework.stereotype.Component;


@Component
public class AccountMapper {

    public AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getOwnerName(),
                account.getAccountType(),
                account.getBalance(),
                account.getVersion(),
                account.getBank().getId()
        );
    }
}
